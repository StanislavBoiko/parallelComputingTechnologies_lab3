package task3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Task3 {
    public static void main(String[] args) {
        Journal journal = new Journal();

        Group group1 = new Group("Група 1");
        Group group2 = new Group("Група 2");
        Group group3 = new Group("Група 3");

        journal.addGroup(group1);
        journal.addGroup(group2);
        journal.addGroup(group3);

        for (int i = 1; i <= 10; i++) {
            group1.addStudent(new Student("Студент " + i + " групи 1"));
            group2.addStudent(new Student("Студент " + i + " групи 2"));
            group3.addStudent(new Student("Студент " + i + " групи 3"));
        }

        Teacher lecturer = new Teacher("Лектор", journal);
        Teacher assistant1 = new Teacher("Асистент 1", journal);
        Teacher assistant2 = new Teacher("Асистент 2", journal);
        Teacher assistant3 = new Teacher("Асистент 3", journal);

        Thread lecturerThread = new Thread(lecturer);
        Thread assistant1Thread = new Thread(assistant1);
        Thread assistant2Thread = new Thread(assistant2);
        Thread assistant3Thread = new Thread(assistant3);

        ArrayList<Thread> threads = new ArrayList<>();
        threads.add(lecturerThread);
        threads.add(assistant1Thread);
        threads.add(assistant2Thread);
        threads.add(assistant3Thread);

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (Thread t: threads) {
            executor.execute(t);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        for (Group group : journal.getGroups()) {
            System.out.println("Оцінки " + group.getGroupName() + ":");
            for (Student student : group.getStudents()) {
                System.out.println(student.getName() + ": " + student.getScore());
            }
            System.out.println();
        }
    }
}

class Teacher implements Runnable {
    private String name;
    private Journal journal;
    private Lock lock = new ReentrantLock();

    public Teacher(String name, Journal journal) {
        this.name = name;
        this.journal = journal;
    }

    @Override
    public void run() {
        for (Group group : journal.getGroups()) {
            for (Student student : group.getStudents()) {
                if (lock.tryLock()) {
                    try {
                        if (student.getScore() == 0) {
                            student.setScore(group, name, (int) (Math.random() * 101));
                            System.out.println("Виставлено оцінку: " + student.getName() + " Виставив: " + name);
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
    }
}

class Student {
    private String name;
    private int score;

    public Student(String name) {
        this.name = name;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(Group group, String teacherName, int score) {
        this.score = score;
    }
}

class Group {
    private String groupName;
    private List<Student> students;

    private Lock lock = new ReentrantLock();

    public Group(String groupName) {
        this.groupName = groupName;
        this.students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public String getGroupName() {
        return groupName;
    }

    public List<Student> getStudents() {
        return students;
    }
}

class Journal {
    private List<Group> groups;

    public Journal() {
        this.groups = new ArrayList<>();
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public List<Group> getGroups() {
        return groups;
    }


}
