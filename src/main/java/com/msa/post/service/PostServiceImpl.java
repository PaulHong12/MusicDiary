package com.msa.post.service;

import com.msa.comment.domain.Comment;
import com.msa.post.domain.Post;
import com.msa.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
public class PostServiceImpl implements PostService {

	private final PostRepository postRepository;

	public PostServiceImpl(PostRepository postRepository) {
		this.postRepository = postRepository;
	}


	@Override
	public Post addPost(String title, String content) {
		// 테스트 코드가 동작하도록 구현
		Post post = new Post(title, content);
		return postRepository.save(post);
	}

	@Override
	public Optional<Post> getPost(long id) {
		return postRepository.findById(id);
	}

	//구현하기
	@Override
	public List<Post> getPostListByUserId() {
		return new ArrayList<>();
	}

	@Override
	// findAll 을 통해서 모든 데이터 조회
	// created_at 기준 내림차순으로 정렬
	public List<Post> getPostList() {
		return postRepository.findAll()
				.stream()
				.sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).toList();
	}

	@Transactional
	public Post addComment(long postId, String content) {
		Optional<Post> postOptional = postRepository.findById(postId);
		if (postOptional.isPresent()) {
			Post post = postOptional.get();
			Comment newComment = new Comment();
			newComment.setContent(content);
			// Assuming Post has a method to add comments
			post.addComment(newComment);
			return postRepository.save(post); // Save the updated post with the new comment
		} else {
			// Handle the case where the post does not exist
			throw new IllegalArgumentException("Post with ID " + postId + " not found.");
		}
	}
	@Override
	public List<Post> getPostsByDate(LocalDate date) {
		return postRepository.findAllByDate(date);
	}

	@Override
	public List<Post> getPostsByDateRange(LocalDate start, LocalDate end) {
		return postRepository.findAllByDateBetween(start, end);
	}

	@Transactional
	public Post updatePost(long postId, String title, String content) {
		Optional<Post> postOptional = postRepository.findById(postId);

		if (postOptional.isPresent()) {
			Post existingPost = postOptional.get();
			existingPost.setTitle(title);
			existingPost.setContent(content);
			return postRepository.save(existingPost); // Save the updated post
		} else {
			// Handle the case where the post does not exist
			throw new IllegalArgumentException("Post with ID " + postId + " not found.");
		}
	}

	@Override
	public void deletePost(long id) {
		postRepository.deleteById(id);
	}

}
