package task4;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Task 4 - Completable Future Helps to Build Open Salary Society
 * <p>
 * Cost: 0.5 points.
 * <p>
 * Assume, we have REST endpoint that returns a list of hired Employees.
 * <p>
 * • REST endpoint is wrapped by Java service class that consuming this endpoint.<p>
 * • Fetch a list of Employee objects asynchronously by calling the hiredEmployees().<p>
 * • Join another CompletionStage<List> that takes care of filling the salary of each hired employee, by calling the getSalary(hiredEmployeeId) method which returns a CompletionStage that asynchronously fetches the salary (again could be consuming a REST endpoint).<p>
 * • When all Employee objects are filled with their salaries, we end up with a List<CompletionStage>, so we call "special operation on CF" to get a final stage that completes upon completion of all these stages.<p>
 * • Print hired Employees with their salaries via "special operation on CF" on final stage.<p>
 * Provide correct solution with CF usage and use appropriate CF operators instead "special operation on CF". Why does the CF usage improve performance here in comparison with synchronous approach? Discuss it with mentor. How thread waiting is implemented in synchronous world?
 */
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

