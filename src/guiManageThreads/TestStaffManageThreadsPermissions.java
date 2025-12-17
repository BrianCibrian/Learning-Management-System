package guiManageThreads;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p> Title: TestStaffManageThreadsPermissions Class </p>
 *
 * <p> Description: test code that verifies staff (role1) users can manage threads
 * (create, rename, delete) in the guiManageThreads area. Outputs the results of
 *  each test in the terminal.</p>
 *
 * @author Omar Munoz
 *
 */
public class TestStaffManageThreadsPermissions { 

	private TestDatabase db;

	@BeforeEach
	public void setUp() {
		db = new TestDatabase();
		FoundationsMain.database = db;
		ControllerManageThreads.theUser = new User() {
			@Override public String getUserName() { return "teststaff"; }
			@Override public boolean getAdminRole() { return false; }
			@Override public boolean getNewRole1() { return true; } 
			@Override public boolean getNewRole2() { return false; }
		};
	}

	/**
	 * test: staff user can create, rename, and delete a thread using the database/controller path.
	 * This verifies the "Manage Threads" operations behave correctly for staff.
	 */
	@Test
	public void test_staffCanCreateRenameDeleteThread() {
		// initial threads from DB
		List<String> initial = ControllerManageThreads.getThreadList();
		assertNotNull(initial);
		// make sure default 'General' is present in the test DB
		assertTrue(initial.contains("General"));

		// Create a new thread testthread1
		String threadA = "testthread1";
		boolean created = FoundationsMain.database.createThread(threadA);
		assertTrue(created, "createThread should work for new thread name");

		// make sure it appears in thread list via controller helper
		List<String> afterCreate = ControllerManageThreads.getThreadList();
		assertTrue(afterCreate.contains(threadA), "Thread list should contain created thread");
		System.out.println("TEST RESULT: test_staffCanCreateRenameDeleteThread - create passed");

		// Rename testthread1 to testthreadRenamed
		String renamed = "testthreadRenamed";
		boolean renamedOk = FoundationsMain.database.updateThreadName(threadA, renamed);
		assertTrue(renamedOk, "updateThreadName should succeed to rename existing thread");

		List<String> afterRename = ControllerManageThreads.getThreadList();
		assertFalse(afterRename.contains(threadA), "Old thread name should be gone after rename");
		assertTrue(afterRename.contains(renamed), "Renamed thread should appear in list");
		System.out.println("TEST RESULT: test_staffCanCreateRenameDeleteThread - rename passed");

		// Delete the renamed thread
		boolean deletedOk = FoundationsMain.database.deleteThread(renamed);
		assertTrue(deletedOk, "deleteThread should succeed for existing thread");

		List<String> afterDelete = ControllerManageThreads.getThreadList();
		assertFalse(afterDelete.contains(renamed), "Deleted thread should not appear in list");
		System.out.println("TEST RESULT: test_staffCanCreateRenameDeleteThread - delete passed");
	}

	/**
	 * test database to mimic Database behavior needed for the manage threads tests.
	 */
	private static class TestDatabase extends Database {
		private final List<String> threads = new ArrayList<>();

		TestDatabase() {
			// default base threads
			threads.add("General");
			threads.add("Announcements");
			threads.add("Help");
			threads.add("Off-topic");
		}

		@Override
		public boolean createThread(String name) {
			if (name == null) return false;
			String n = name.trim();
			if (n.isEmpty()) return false;
			for (String t : threads) {
				if (t.equalsIgnoreCase(n)) return false;
			}
			threads.add(n);
			return true;
		}

		@Override
		public boolean updateThreadName(String oldName, String newName) {
			if (oldName == null || newName == null) return false;
			String o = oldName.trim();
			String n = newName.trim();
			if (o.isEmpty() || n.isEmpty()) return false;
			int idx = -1;
			for (int i = 0; i < threads.size(); i++) {
				if (threads.get(i).equalsIgnoreCase(o)) {
					idx = i;
					break;
				}
			}
			if (idx == -1) return false;
			// don't allow duplicate name
			for (String t : threads) if (t.equalsIgnoreCase(n)) return false;
			threads.set(idx, n);
			return true;
		}

		@Override
		public boolean deleteThread(String name) {
			if (name == null) return false;
			String n = name.trim();
			if ("General".equalsIgnoreCase(n)) return false; // protect General (default database, so if a thread does get deleted all posts from that thread go to general)
			for (int i = 0; i < threads.size(); i++) {
				if (threads.get(i).equalsIgnoreCase(n)) {
					threads.remove(i);
					return true;
				}
			}
			return false;
		}

		@Override
		public List<String> getThreads() {
			return new ArrayList<>(threads);
		}
	}
}
