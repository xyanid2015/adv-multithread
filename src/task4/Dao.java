package task4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

public class Dao {
    private static final List<Employee> employees;
    private static final Map<String, Integer> salaries = new HashMap<>();

    static {
        Random random = new Random();
        employees = IntStream
                .rangeClosed(1, 100)
                .mapToObj(i -> new Employee("emp_".concat(String.valueOf(i)), null))
                .toList();
        for (Employee employee : employees) {
            salaries.put(employee.getId(), random.nextInt(20000, 100000));
        }
    }

    public static List<Employee> hiredEmployees() {
        return employees.stream().map(Employee::clone).toList();
    }

    public static Integer getSalary(String hiredEmployeeId) {
        return salaries.get(hiredEmployeeId);
    }
}
