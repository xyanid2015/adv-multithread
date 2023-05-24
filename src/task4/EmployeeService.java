package task4;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class EmployeeService {
    private static final EmployeeService employeeService = new EmployeeService();

    private CompletableFuture<List<Employee>> hiredEmployees() {
        return CompletableFuture.supplyAsync(Dao::hiredEmployees);
    }

    private CompletableFuture<Integer> getSalary(String hiredEmployeeId) {
        return CompletableFuture.supplyAsync(() -> Dao.getSalary(hiredEmployeeId));
    }

    private CompletableFuture<List<Employee>> getHiredEmployeesWithSalaries() {
        return hiredEmployees().thenCompose(employees -> {
            List<CompletableFuture<Employee>> futures =
                    employees.stream().map(employee ->
                            getSalary(employee.getId()).thenApply(salary -> {
                                employee.setSalary(salary);
                                return employee;
                            })
                    ).toList();

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0])
            );

            return allFutures.thenApply(v ->
                    futures.stream().map(CompletableFuture::join).collect(Collectors.toList())
            );
        });
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<List<Employee>> hiredEmployeesWithSalaries = employeeService.getHiredEmployeesWithSalaries();
        for (Employee employee : hiredEmployeesWithSalaries.get()) {
            System.out.println("employee = " + employee);
        }
    }
}

