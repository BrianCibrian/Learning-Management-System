package guiViewPosts;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import guiViewPosts.ControllerViewPosts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p> Title: TestStaffDeletePermissions Class </p>
 *
 * <p> Description: test code that verifies staff (role1) users
 * can visually delete posts and replies. Outputs the results 
 * of each test in the terminal.</p> 
 *
 * @author Omar Munoz
 *
 */
public class TestStaffDeletePermissions { 

	private TestDatabase db;

	@BeforeEach
	public void setUp() {
		db = new TestDatabase();
		FoundationsMain.database = db;
		ControllerViewPosts.theUser = null; 
	}

	/**
	 * test: staff user can visually delete a post made by another user.
	 */
	@Test
	public void test_staffCanVisuallyDeletePost() { 
		// create a post made by a test author
		Post p = new Post();
		p.setTitle("Test Title Original");
		p.setBody("Test body content");
		p.setAuthorUsername("testidk1");
		int postId = db.createPost(p);

		// create a staff user (role1)
		User testStaff = new User() {
			@Override public String getUserName() { return "teststaff"; }
			@Override public boolean getAdminRole() { return false; }
			@Override public boolean getNewRole1() { return true; }  
			@Override public boolean getNewRole2() { return false; }
		};

		ControllerViewPosts.theUser = testStaff;

		boolean isAdminOrStaff = (ControllerViewPosts.theUser != null &&
				(ControllerViewPosts.theUser.getAdminRole() || ControllerViewPosts.theUser.getNewRole1()));

		assertTrue(isAdminOrStaff, "Staff should be treated as admin-or-staff (same permissions) for delete permission");

		boolean ok = ControllerViewPosts.performVisualDelete(postId);
		assertTrue(ok, "performVisualDelete should work");

		Post after = db.getPostById(postId);
		assertEquals("Post deleted", after.getTitle(), "Title should be replaced with 'Post deleted'");
		assertEquals("", after.getBody(), "Body should be cleared after visual delete");

		System.out.println("TEST RESULT: test_staffCanVisuallyDeletePost - verified staff visual post delete");
	}

	/**
	 * test: staff user can visually delete a reply.
	 */
	@Test
	public void test_staffCanVisuallyDeleteReply() {
		// create a post and a reply made by a test author
		Post p = new Post();
		p.setTitle("Test Title For Replies");
		p.setBody("Test body");
		p.setAuthorUsername("testidk1");
		int postId = db.createPost(p);

		Reply r = new Reply();
		r.setPostId(postId);
		r.setContent("Test reply content");
		r.setAuthorUsername("testidk1");
		int replyId = db.createReply(r);

		// staff user
		User testStaff = new User() {
			@Override public String getUserName() { return "teststaff"; }
			@Override public boolean getAdminRole() { return false; }
			@Override public boolean getNewRole1() { return true; } 
			@Override public boolean getNewRole2() { return false; }
		};

		ControllerViewPosts.theUser = testStaff;

		boolean visible = (ControllerViewPosts.theUser != null &&
				(ControllerViewPosts.theUser.getAdminRole() || ControllerViewPosts.theUser.getNewRole1()));
		assertTrue(visible, "Delete reply button should be visible to staff");

		boolean ok = FoundationsMain.database.updateReplyToDeletedVisual(replyId);
		assertTrue(ok, "updateReplyToDeletedVisual should work");

		Reply after = db.getReplyById(replyId);
		assertEquals("Reply deleted", after.getContent(), "Reply content should be replaced with 'Reply deleted'");

		System.out.println("TEST RESULT: test_staffCanVisuallyDeleteReply - verified staff visual reply delete");
	}

	/**
	 * Test database to mimic Database of the project
	 */
	private static class TestDatabase extends Database {
		private final java.util.Map<Integer, Post> posts = new java.util.HashMap<>();
		private final java.util.Map<Integer, Reply> replies = new java.util.HashMap<>();
		private int nextPostId = 1;
		private int nextReplyId = 1;

		@Override
		public int createPost(Post p) {
			int id = nextPostId++;
			p.setId(id);
			posts.put(id, p);
			return id;
		}

		@Override
		public int createReply(Reply r) {
			int id = nextReplyId++;
			r.setId(id);
			replies.put(id, r);
			return id;
		}

		@Override
		public boolean updatePostToDeletedVisual(int postId) {
			Post p = posts.get(postId);
			if (p == null) return false;
			p.setTitle("Post deleted");
			p.setBody("");
			try { p.setDeleted(true); } catch (Throwable ignored) {}
			return true;
		}

		@Override
		public boolean updateReplyToDeletedVisual(int replyId) {
			Reply r = replies.get(replyId);
			if (r == null) return false;
			r.setContent("Reply deleted");
			return true;
		}

		// helper getters for the tests
		public Post getPostById(int id) {
			return posts.get(id);
		}

		public Reply getReplyById(int id) {
			return replies.get(id);
		}

		@Override
		public java.util.List<Post> getPosts() { return new java.util.ArrayList<>(posts.values()); }

		@Override
		public java.util.List<Reply> getRepliesForPost(int postId) {
			java.util.List<Reply> out = new java.util.ArrayList<>();
			for (Reply r : replies.values()) if (r.getPostId() == postId) out.add(r);
			return out;
		}
	}
}
