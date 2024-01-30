package org.emp.employeeResource;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;


import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.emp.entity.Employee;
import org.jboss.resteasy.reactive.RestResponse;

import java.net.URI;
import java.util.List;

import static jakarta.ws.rs.core.Response.Status.*;
import static org.hibernate.Hibernate.map;

//***Uni for single-valued responses and Multi for streams of data****.
//POST /employees - Creates a new employee (returns Uni<Employee>)
//GET /employees/{id} - Retrieves an employee by ID (returns Uni<Employee>)
//GET /employees - Retrieves all employees (returns Multi<Employee>)
//PATCH /employees/{id} - Updates an employee by ID (returns Uni<Employee>)
//DELETE /employees/{id} - Deletes an employee by ID (returns Uni<Void>)
@Path("/employeHandler")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeResource {
    @GET
    public Uni<List<Employee>> get() {
        return Employee.listAll(Sort.by("name"));
    }

    @GET
    @Path("{/id}")
    public Uni<Employee> getById(@PathParam("id") int id){
        return Employee.findById(id);
    }

    @POST
    public Uni<RestResponse<Employee>> create(Employee employee) {
//        Panache.withTransaction is a utility method provided by Quarkus for handling transactions.
//        It ensures that the given operation (in this case, persisting the employee) is executed within a transaction.
//        employee::persist is a method reference that invokes the persist method on the employee object.
//        replaceWith operator transforms that result into a RestResponse with a 201 Created status
//        and the created employee as the response body.
        return Panache.withTransaction(employee::persist).replaceWith(RestResponse.status(CREATED, employee));
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Long id, Employee fruit) {
        return Panache
                .withTransaction(() -> Employee.<Employee> findById(id)
                        .onItem().ifNotNull().invoke(entity -> entity.setName(fruit.getName()))
                )
                .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                .onItem().ifNull().continueWith(Response.ok().status(NOT_FOUND)::build);
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> deleteById(Long id) {
        return Panache.withTransaction(() -> Employee.deleteById(id))
                .map(deleted -> deleted
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }
//    @DELETE
//    public Uni<Response> deleteAll(){
//         return Panache.withTransaction(()->Employee.deleteAll())
//                 .map(delete1 -> delete1
//                         ? Response.ok().status(NO_CONTENT).build()
//                         : Response.ok().status(NOT_FOUND).build());
//    }

    @DELETE
    public Uni<Response> deleteAll() {
        return Panache.withTransaction(() -> Employee.deleteAll())
                .map(delete1 -> Boolean.valueOf(delete1.intValue() > 0)
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }

}

