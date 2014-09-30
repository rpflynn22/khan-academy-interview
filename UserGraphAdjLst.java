import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Represents a graph of users. Each vertex represents a user, and depending on
 * which infection method is being called, the graph can either be viewed as
 * directed (limited infection) or undirected (total infection). In either
 * case, graph edges represent a student-teacher connection, teacher-student
 * connection, or classmate-classmate connection.
 * @author Ryan P Flynn
 *
 */
public class UserGraphAdjLst {
	
	/**
	 * Initializes the User Graph. Sets the adjacency list to a new HashMap.
	 */
	public UserGraphAdjLst() {
		_adjLst = new HashMap<Person, ArrayList<ArrayList<Person>>>();
		_bigAdjLst = new HashMap<Person, HashSet<Person>>();
		_visited = new HashSet<Person>();
	}
	
	/**
	 * Adds a teacher to the adjacency list with a new (empty) ArrayList value.
	 * @param teacher The teacher to be added as a key in the adjacency list.
	 * @return True if the teacher does not already exist and is added.
	 *         False if the teacher does already exist and is not added.
	 */
	public boolean addTeacher(Person teacher) {
		if (! _adjLst.keySet().contains(teacher)) {
			ArrayList<ArrayList<Person>> classes = new
					ArrayList<ArrayList<Person>>();
			_adjLst.put(teacher, classes);
			_bigAdjLst.put(teacher, new HashSet<Person>());
			return true;
		}
		return false;
	}
	
	/**
	 * Adds a class to a teacher's list of classes.
	 * @param  teacher  The teacher whose list of classes will be appended to.
	 * @param  students The class that will be appended to the teacher's list.
	 * @return True if the teacher exists as a key. False otherwise.
	 */
	public boolean addClass(Person teacher, ArrayList<Person> students) {
		boolean found = _adjLst.keySet().contains(teacher);
		if (found) {
			ArrayList<ArrayList<Person>> existingClasses = _adjLst.get(teacher);
			existingClasses.add(students);
			HashSet<Person> teacherConns = _bigAdjLst.get(teacher);
			for (Person student : students) {
				teacherConns.add(student);
				HashSet<Person> studentConns = null;
				if (_bigAdjLst.containsKey(student)) {
					studentConns = _bigAdjLst.get(student);
				} else {
					studentConns = new HashSet<Person>();
				}
				for (Person otherStudent: students) {
					if ((!(otherStudent == student)) &&
							!studentConns.contains(otherStudent)) {
						studentConns.add(otherStudent);
					}
				}
				studentConns.add(teacher);
				_bigAdjLst.put(student, studentConns);
			}
			_bigAdjLst.put(teacher, teacherConns);
			return true;
		}
		return false;
	}
	
	/**
	 * Launches a total infection in which any users who are connected
	 * (teacher-student, student-teacher, classmate-classmate) become infected
	 * if they are reachable from the given user.
	 * @param node Represents the given user from which the infection should be
	 *             launched.
	 */
	public void totalInfection(Person node) {
		_visited.clear();
		totalInfectionHelper(node);
		_visited.clear();
	}
	
	/**
	 * Used so that _visited may be reset before and after being used.
	 * @param node Represents the given user from which the infection should be
	 *             launched.
	 */
	public void totalInfectionHelper(Person node) {
		totalInfectionVisit(node);
		for (Person person : _bigAdjLst.get(node)) {
			if (! _visited.contains(person)) {
				totalInfectionHelper(person);
			}
		}
	}
	
	/**
	 * Add the user to the list of visited users and update the user's version.
	 * @param node
	 */
	public void totalInfectionVisit(Person node) {
		_visited.add(node);
		if (!node.getVersion()) {
			node.updateVersion();
		}
	}
	
	/**
	 * Kick off a limited infection starting with the given teacher and going
	 * until num users have been infected. Or until the entire graph has been
	 * infected. Whichever comes first.
	 * @param teacher The teacher to start the infection at.
	 * @param num     The number of users we want to infect.
	 */
	public void limitedInfection(Person teacher, int num) {
		num -= infectClass(teacher);
		while (num > 0) {
			Person newTeacher = pickBestTeacher();
			if (newTeacher == null) {
				return;
			}
			if (newTeacher != null) {
				num -= infectClass(newTeacher);
			}
		}
	}
	
	/**
	 * Infect a teacher and each of the teacher's students, if they have not
	 * already been infected.
	 * @param teacher The teacher who will be infected, along with his/her
	 *                students.
	 * @return The number of users infected in this process.
	 */
	public int infectClass(Person teacher) {
		int numInfected;
		if (teacher.getVersion()) {
			numInfected = 0;
		} else {
			numInfected = 1;
			teacher.updateVersion();
		}
		for (ArrayList<Person> clas : _adjLst.get(teacher)) {
			for (Person student : clas) {
				if (!student.getVersion()) {
					numInfected++;
					student.updateVersion();
				}
			}
		}
		return numInfected;
	}
	
	/**
	 * Picks the ideal teacher to infect next. Spec said to choose the teacher
	 * with the most number of students infected already. This checks for each
	 * teacher, how many of his/her students are already infected. It will pick
	 * the teacher with the most, provided that either the teacher has not yet
	 * been infected or the entire class has not already been infected. If both
	 * of those conditions were true (i.e. the teacher is already infected and
	 * so are all of his/her students), then launching a limited infection from
	 * that teacher would not do anything. Also will pick a backup teacher who
	 * has no infected students and is not infected his/herself.
	 * @return The ideal teacher (or a not-very-random second choice).
	 */
	public Person pickBestTeacher() {
		Person bestTeacher = null;
		Person backupTeacher = null;
		int maxStudentsInfected = 0;
		for (Person teacher : _adjLst.keySet()) {
			int teachersStudentsInf = 0;
			int teachersStudents = 0;
			for (ArrayList<Person> clas : _adjLst.get(teacher)) {
				for (Person student : clas) {
					teachersStudents++;
					if (student.getVersion()) {
						teachersStudentsInf++;
					}
				}
			}
			if (teachersStudentsInf > maxStudentsInfected) {
				if (!teacher.getVersion() ||
						teachersStudentsInf < teachersStudents) {
					maxStudentsInfected = teachersStudentsInf;
					bestTeacher = teacher;
				}
			} else if (bestTeacher == null && !(teacher.getVersion() &&
					teachersStudentsInf == teachersStudents)) { 
				backupTeacher = teacher;
			}
		}
		if (bestTeacher == null) {
			return backupTeacher;
		} else {
			return bestTeacher;
		}
	}
	
	/**
	 * Prints out the people in the UserGraph in an organized format. Useful in
	 * testing. Format:
	 * teacher\n\tstudents in class\n\tstudents in class...
	 */
	public void showState() {
		for (Person teacher : _adjLst.keySet()) {
			System.out.println(teacher.getName());
			for (ArrayList<Person> students : _adjLst.get(teacher)) {
				System.out.print("\t");
				for (Person student : students) {
					System.out.print(student.getName() + ", ");
				}
				System.out.print("\n");
			}
		}
	}
	
	/**
	 * Prints out _bigAdjLst in an organized format. Shows every connection in
	 * the graph for each user.
	 */
	public void showFullAdjState() {
		for (Person person : _bigAdjLst.keySet()) {
			System.out.print(person.getName() + ": ");
			for (Person per : _bigAdjLst.get(person)) {
				System.out.print(per.getName() + ", ");
			}
			System.out.print("\n");
		}
	}
	
	/**
	 * Shows the number of people in the graph who have been infected.
	 * @return The number of infected users.
	 */
	public int numInfected() {
		int count = 0;
		for (Person person : _bigAdjLst.keySet()) {
			if (person.getVersion()) {
				count += 1;
			}
		}
		return count;
	}
	
	/**
	 * For each user, shows whether or not user is using the new version.
	 */
	public void showVersionStates() {
		for (Person person : _bigAdjLst.keySet()) {
			System.out.println(person.getName()
					+ " " + Boolean.toString(person.getVersion()));
		}
	}
	
	/**
	 * Adjacency list for coaching relationships. Key is teacher (Person
	 * object), and the value is a 2D ArrayList, where the outer list 
	 * is a list of classrooms, and each classroom is a list of students
	 * (also Person objects).
	 */
	private HashMap<Person, ArrayList<ArrayList<Person>>> _adjLst;
	
	/**
	 * Useful for total infection, this field represents an adjacency matrix
	 * from teacher to student, classmate to classmate, and student to teacher.
	 */
	private HashMap<Person, HashSet<Person>> _bigAdjLst;
	
	/**
	 * This field is useful for traversals and exploring the graph.
	 * To be reset after each use.
	 */
	private HashSet<Person> _visited;
}
