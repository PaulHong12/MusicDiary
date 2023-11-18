package com.msa.post.domain;

import com.msa.comment.domain.Comment;
import com.msa.member.domain.Member;
import com.msa.post.dto.PostDto;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.*;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Post {

	@Id
	@Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private long id;

	@Column(name = "creator")
	private String creator;

	@Column(name="title")
	private String title;

	@Column(name="content", columnDefinition="TEXT")
	private String content;

	//
	@Column(name = "date")
	private LocalDate date = LocalDate.now();

	@Column(name="thumbnail")
	private String thumbnail; // URL or path of the thumbnail image

	@CreatedDate
	private LocalDateTime createdAt = LocalDateTime.now();

	@LastModifiedDate
	private LocalDateTime updatedAt = LocalDateTime.now();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Comment> comments;
	
	public Post() {
		super();
	}

	public Post(String title, String content, String username) {
		this.title = title;
		this.content = content;
		this.creator = username;
	}
	public Post(String title, String content, String username, String thumbnail) {
		this.title = title;
		this.content = content;
		this.creator = username;
		this.thumbnail = thumbnail;
	}
	public Post(String title, String content, Set<Comment> comments) {
		this.title = title;
		this.content = content;
		this.comments = comments;
	}

	public String getCreator(){
		return this.creator;
	}

	public PostDto convert2DTO() {
		return new PostDto(this.getTitle(), this.getContent(),this.creator);
	}

	public LocalDate getDate() {
		return this.createdAt.toLocalDate(); //debug
	}

	public void addComment(Comment newComment) {
		if (comments == null) {
			comments = new HashSet<>();
		}
		comments.add(newComment);
		newComment.setPost(this);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getId() {
		return id;
	}
}
