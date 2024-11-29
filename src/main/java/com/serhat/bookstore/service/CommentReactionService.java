package com.serhat.bookstore.service;

import com.serhat.bookstore.Repository.CommentReactionRepo;
import com.serhat.bookstore.Repository.CommentRepository;
import com.serhat.bookstore.Repository.CustomerRepository;
import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.exception.CommentNotFoundException;
import com.serhat.bookstore.exception.CustomerNotFoundException;
import com.serhat.bookstore.model.Comment;
import com.serhat.bookstore.model.CommentReaction;
import com.serhat.bookstore.model.Customer;
import com.serhat.bookstore.model.Reaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentReactionService {
    private final CommentRepository commentRepository;
    private final CommentReactionRepo commentReactionRepo;
    private final CustomerRepository customerRepository;

    public String getUsername(Principal principal) {
        return principal.getName().toLowerCase();
    }

    @Transactional
    public LikeResponse likeComment(LikeRequest request, Principal principal) {
        String username = getUsername(principal);
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + username));
        Comment comment = commentRepository.findById(request.commentId())
                .orElseThrow(() -> new CommentNotFoundException("Comment not found: " + request.commentId()));

        Customer commentAuthor = comment.getCustomer();

        Optional<CommentReaction> existingReaction = commentReactionRepo.findByCustomerAndComment(customer, comment);

        if (existingReaction.isPresent()) {
            Reaction currentReaction = existingReaction.get().getReaction();

            if (currentReaction == Reaction.LIKED) {
                processReactionCounters(customer, commentAuthor, comment, -1, Reaction.NEUTRAL);
                log.info("Like cancelled for comment: " + comment.getCommentId());
                return new LikeResponse("Like is Cancelled", customer.getUsername(), comment.getComment());
            }

            if (currentReaction == Reaction.NEUTRAL) {
                processReactionCounters(customer, commentAuthor, comment, 1, Reaction.LIKED);
                log.info("Liked comment: " + comment.getCommentId());
                return new LikeResponse("Message liked successfully", customer.getUsername(), comment.getComment());
            }

            if (currentReaction == Reaction.DISLIKED) {
                log.info("Dislike cancelled, changing to like for comment: " + comment.getCommentId());
                processReactionCounters(customer, commentAuthor, comment, 1, Reaction.LIKED);
                return new LikeResponse("Message liked successfully", customer.getUsername(), comment.getComment());
            }
        }

        processReactionCounters(customer, commentAuthor, comment, 1, Reaction.LIKED);
        log.info("Like added for comment: " + comment.getCommentId());

        return new LikeResponse("Message liked successfully", customer.getUsername(), comment.getComment());
    }

    @Transactional
    public DislikeResponse dislikeComment(DislikeRequest request, Principal principal) {
        String username = getUsername(principal);
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + username));
        Comment comment = commentRepository.findById(request.commentId())
                .orElseThrow(() -> new CommentNotFoundException("Comment not found: " + request.commentId()));

        Customer commentAuthor = comment.getCustomer();

        Optional<CommentReaction> existingReaction = commentReactionRepo.findByCustomerAndComment(customer, comment);

        if (existingReaction.isPresent()) {
            Reaction currentReaction = existingReaction.get().getReaction();

            if (currentReaction == Reaction.DISLIKED) {
                processReactionCounters(customer, commentAuthor, comment, -1, Reaction.NEUTRAL);
                log.info("Dislike cancelled for comment: " + comment.getCommentId());
                return new DislikeResponse("Dislike is Cancelled", customer.getUsername(), comment.getComment());
            }

            if (currentReaction == Reaction.NEUTRAL) {
                processReactionCounters(customer, commentAuthor, comment, 1, Reaction.DISLIKED);
                log.info("Disliked comment: " + comment.getCommentId());
                return new DislikeResponse("Message disliked successfully", customer.getUsername(), comment.getComment());
            }

            if (currentReaction == Reaction.LIKED) {
                log.info("Like cancelled, changing to dislike for comment: " + comment.getCommentId());
                processReactionCounters(customer, commentAuthor, comment, 1, Reaction.DISLIKED);
                return new DislikeResponse("Message disliked successfully", customer.getUsername(), comment.getComment());
            }
        }

        processReactionCounters(customer, commentAuthor, comment, 1, Reaction.DISLIKED);
        log.info("Dislike added for comment: " + comment.getCommentId());

        return new DislikeResponse("Message disliked successfully", customer.getUsername(), comment.getComment());
    }

    @Transactional
    public RepostResponse repostComment(RepostRequest request, Principal principal) {
        String username = getUsername(principal);
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + username));
        log.info("Customer reposted : " + username);

        Comment comment = commentRepository.findById(request.commentId())
                .orElseThrow(() -> new CommentNotFoundException("Comment not found : " + request.commentId()));

        Customer commentAuthor = comment.getCustomer();
        log.info("Comment author : " + commentAuthor.getUsername());

        Optional<CommentReaction> existingReaction = commentReactionRepo.findByCustomerAndComment(customer, comment);

        if (existingReaction.isPresent()) {
            Reaction currentReaction = existingReaction.get().getReaction();

            if (currentReaction == Reaction.REPOSTED) {
                processReactionCounters(customer, commentAuthor, comment, -1, Reaction.NEUTRAL);
                log.info("Repost cancelled for comment: " + comment.getCommentId());
                return new RepostResponse("Repost is Cancelled", customer.getUsername(), comment.getComment());
            }

            if (currentReaction == Reaction.NEUTRAL) {
                processReactionCounters(customer, commentAuthor, comment, 1, Reaction.REPOSTED);
                log.info("Reposted comment: " + comment.getCommentId());
                return new RepostResponse("Message reposted successfully", customer.getUsername(), comment.getComment());
            }
        }

        processReactionCounters(customer, commentAuthor, comment, 1, Reaction.REPOSTED);
        log.info("Repost made for comment: " + comment.getCommentId());

        return new RepostResponse("Repost saved successfully", customer.getUsername(), comment.getComment());
    }


    @Transactional
    public void processReactionCounters(Customer customer, Customer commentAuthor, Comment comment, int value, Reaction reaction) {
        Optional<CommentReaction> existingReaction = commentReactionRepo.findByCustomerAndComment(customer, comment);

        if (reaction == Reaction.NEUTRAL) {
            if (existingReaction.isPresent()) {
                Reaction currentReaction = existingReaction.get().getReaction();

                if (currentReaction == Reaction.LIKED) {
                    customer.setTotalLikes(customer.getTotalLikes() - 1);
                    commentAuthor.setTotalLikesReceived(commentAuthor.getTotalLikesReceived() - 1);
                    comment.setLikes(comment.getLikes() - 1);
                } else if (currentReaction == Reaction.DISLIKED) {
                    customer.setTotalDislikes(customer.getTotalDislikes() - 1);
                    commentAuthor.setTotalDislikesReceived(commentAuthor.getTotalDislikesReceived() - 1);
                    comment.setDislikes(comment.getDislikes() - 1);
                } else if (currentReaction == Reaction.REPOSTED) {
                    customer.setTotalReposts(customer.getTotalReposts() - 1);
                    commentAuthor.setTotalRepostsReceived(commentAuthor.getTotalRepostsReceived() - 1);
                    comment.setRepost(comment.getRepost() - 1);
                }

                existingReaction.get().setReaction(Reaction.NEUTRAL);
                commentReactionRepo.save(existingReaction.get());
            }
        } else {
            if (existingReaction.isPresent()) {
                Reaction currentReaction = existingReaction.get().getReaction();

                if (currentReaction != reaction) {
                    switch (currentReaction) {
                        case LIKED -> {
                            customer.setTotalLikes(customer.getTotalLikes() - 1);
                            commentAuthor.setTotalLikesReceived(commentAuthor.getTotalLikesReceived() - 1);
                            comment.setLikes(comment.getLikes() - 1);
                        }
                        case DISLIKED -> {
                            customer.setTotalDislikes(customer.getTotalDislikes() - 1);
                            commentAuthor.setTotalDislikesReceived(commentAuthor.getTotalDislikesReceived() - 1);
                            comment.setDislikes(comment.getDislikes() - 1);
                        }
                        case REPOSTED -> {
                            customer.setTotalReposts(customer.getTotalReposts() - 1);
                            commentAuthor.setTotalRepostsReceived(commentAuthor.getTotalRepostsReceived() - 1);
                            comment.setRepost(comment.getRepost() - 1);
                        }
                    }

                    existingReaction.get().setReaction(reaction);
                    commentReactionRepo.save(existingReaction.get());
                }
            } else {
                CommentReaction newReaction = new CommentReaction();
                newReaction.setCustomer(customer);
                newReaction.setComment(comment);
                newReaction.setReaction(reaction);
                commentReactionRepo.save(newReaction);
            }

            switch (reaction) {
                case DISLIKED -> {
                    customer.setTotalDislikes(customer.getTotalDislikes() + value);
                    commentAuthor.setTotalDislikesReceived(commentAuthor.getTotalDislikesReceived() + value);
                    comment.setDislikes(comment.getDislikes() + value);
                }
                case LIKED -> {
                    customer.setTotalLikes(customer.getTotalLikes() + value);
                    commentAuthor.setTotalLikesReceived(commentAuthor.getTotalLikesReceived() + value);
                    comment.setLikes(comment.getLikes() + value);
                }
                case REPOSTED -> {
                    customer.setTotalReposts(customer.getTotalReposts() + value);
                    commentAuthor.setTotalRepostsReceived(commentAuthor.getTotalRepostsReceived() + value);
                    comment.setRepost(comment.getRepost() + value);
                }
            }
        }

        customerRepository.save(customer);
        customerRepository.save(commentAuthor);
        commentRepository.save(comment);
    }



}

