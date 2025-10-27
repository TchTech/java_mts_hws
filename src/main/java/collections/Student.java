package collections;

import java.util.Objects;

/**
 * Модель студента.
 */
public final class Student {
    private final int id;
    private final String name;
    private final double grade;

    /**
     * @param id идентификатор
     * @param name имя (не null)
     * @param grade оценка
     */
    public Student(int id, String name, double grade) {
        if (name == null) throw new NullPointerException("name");
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getGrade() { return grade; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student s = (Student) o;
        return id == s.id && Double.compare(s.grade, grade) == 0 && name.equals(s.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, grade);
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', grade=" + grade + "}";
    }
}
