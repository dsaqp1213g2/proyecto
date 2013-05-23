package dsa.tasting.rest;

import java.net.URI;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import dsa.tasting.rest.model.User;
import dsa.tasting.rest.util.APIErrorBuilder;
import dsa.tasting.rest.util.DataSourceSAP;



@Path("/users/{username}")
public class UserResource {
	
	@Context
	HttpServletRequest request;
	
	@Context
	private SecurityContext security;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)	//distingue el tipo peticion que le llega
	public User getUserJSON ( @PathParam("username") String username ){   // si es JSON llamara a esta funcion
		return getUser(username);
	}
	@GET
	@Produces(MediaType.APPLICATION_XML)   // si es XML llamara a la proxima funcion
	public JAXBElement<User> getUserXML ( @PathParam("username") String username ){
		return new JAXBElement<User>(new QName("user"), User.class, getUser(username));
	}
	
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUserJSON(@PathParam("username") String username){
		deleteUser(username);
		return Response.status(204).build();
	}
	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	public Response deleteUserXML(@PathParam("username") String username){
		deleteUser(username);
		return Response.status(204).build();
	}
	
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUserJSON(@PathParam("username") String username, User user){
		updateUser(username, user);
		Response response=null;
		try{
			response = Response.status(204).location(new URI("/users"+username)).build();
		}catch(Exception e){}
			
		return response;
	}
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response updateUserJSON(@PathParam("username") String username, JAXBElement<User> user){
		updateUser(username, user.getValue());
		Response response=null;
		try{
			response = Response.status(204).location(new URI("/users"+username)).build();
		}catch(Exception e){}
			
		return response;
	}
	
	

	
	//los 2 van a la base de datos y recuperan el usuario con nombre "username" que haya en la base de datos
	
	public User getUser(String username) {
		Connection connection = null;
		try {
			connection = DataSourceSAP.getInstance().getDataSource().getConnection();
		} catch (SQLException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.SERVICE_UNAVAILABLE)
							.entity(APIErrorBuilder.buildError(
									Response.Status.SERVICE_UNAVAILABLE
											.getStatusCode(),
									"Service unavailable.", request)).build());
		}

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("select * from usuarios where user='"+username+"'");
			if (!rs.next()){
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"User not found.", request)).build());
			}

			User user = new User();
			// obligatorios: id_user, user, nombre, telefono, email, sexo,
			user.setId_user(rs.getInt("id_user"));
			user.setUser(rs.getString("user"));
			user.setNombre(rs.getString("nombre"));
			user.setTelefono(rs.getString("telefono"));
			user.setEmail(rs.getString("email"));
			user.setSexo(rs.getString("sexo"));
			user.setCiudad(rs.getString("ciudad"));
			// no obligatorios: los demás
			user.setNacionalidad(rs.getString("nacionalidad"));
			user.setEstado_civil(rs.getString("estado_civil"));
			user.setPelo(rs.getString("pelo"));
			user.setOjos(rs.getString("ojos"));
			user.setEstatura(rs.getString("estatura"));
			user.setComplexion(rs.getString("complexion"));
			user.setEmpleo(rs.getString("empleo"));
			user.setHijos(rs.getString("hijos"));
			user.setDescripcion(rs.getString("descripcion"));
			user.setPreferencias(rs.getString("Preferencias"));


			stmt.close();
			connection.close();
			return user;
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}
	}

	private void deleteUser(String username){
		
		if (security.isUserInRole("registered")) {
			if(!security.getUserPrincipal().getName().equals(username)){
				throw new WebApplicationException(Response
						.status(Response.Status.FORBIDDEN)
						.entity(APIErrorBuilder.buildError(
								Response.Status.FORBIDDEN
										.getStatusCode(),
								"Forbiden. No puedes borrar a otro usuario.", request)).build());
			}
		}
		
		Connection connection = null;
		try {
			connection = DataSourceSAP.getInstance().getDataSource().getConnection();
		} catch (SQLException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.SERVICE_UNAVAILABLE)
							.entity(APIErrorBuilder.buildError(
									Response.Status.SERVICE_UNAVAILABLE.getStatusCode(),
									"Service unavailable.", request)).build());
		}

		try {
			
			Statement stmt = connection.createStatement();
			int i = stmt.executeUpdate("delete from roles where user='"+ username + "'");
			
			int a = stmt.executeUpdate("delete from usuarios where user='"+ username + "'");
			
			if (a==0 || i==0){
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"User not found.", request)).build());
			}
			
			stmt.close();
			connection.close();
			
			System.out.println("Comandos delete:");
			System.out.println("delete from roles where user='"+ username + "'");
			System.out.println("delete from usuarios where user='"+ username + "'");
			System.out.println("Delete realizado correctamente.");
			
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}
	}
	
	private void updateUser(String username, User user){
		
		if (security.isUserInRole("registered")) {
			if(!security.getUserPrincipal().getName().equals(username)){
				throw new WebApplicationException(Response
						.status(Response.Status.FORBIDDEN)
						.entity(APIErrorBuilder.buildError(
								Response.Status.FORBIDDEN
										.getStatusCode(),
								"Forbiden. No puedes actualizar a otro usuario.", request)).build());
			}
		}
		
		Connection connection = null;
		try {
			connection = DataSourceSAP.getInstance().getDataSource().getConnection();
		} catch (SQLException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.SERVICE_UNAVAILABLE)
							.entity(APIErrorBuilder.buildError(
									Response.Status.SERVICE_UNAVAILABLE.getStatusCode(),
									"Service unavailable.", request)).build());
		}

		try {
			Statement stmt = connection.createStatement();
			
			StringBuilder sb = new StringBuilder("update usuarios set ");
			
			
			if (user.getNombre() == null || user.getNombre() == ""){
				throw new WebApplicationException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(APIErrorBuilder.buildError(
								Response.Status.BAD_REQUEST
										.getStatusCode(),
								"Bad request. El nombre no puede ser null.", request)).build());//400=> bad request
			}else{
				sb.append("nombre = '"+user.getNombre()+"', ");
			}
			
			if (user.getFecha() == null || user.getFecha() == ""){
				throw new WebApplicationException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(APIErrorBuilder.buildError(
								Response.Status.BAD_REQUEST
										.getStatusCode(),
								"Bad request. La fecha no puede ser null.", request)).build());//400=> bad request
			}else{
				sb.append("fecha = '"+user.getFecha()+"', ");
			}
			
			if (user.getTelefono() == null || user.getTelefono() == ""){
				throw new WebApplicationException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(APIErrorBuilder.buildError(
								Response.Status.BAD_REQUEST
										.getStatusCode(),
								"Bad request. El telefono no puede ser null.", request)).build());//400=> bad request
			}else{
				sb.append("telefono = '"+user.getTelefono()+"', ");
			}
			
			if (user.getEmail() == null || user.getEmail() == ""){
				throw new WebApplicationException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(APIErrorBuilder.buildError(
								Response.Status.BAD_REQUEST
										.getStatusCode(),
								"Bad request. El email no puede ser null.", request)).build());//400=> bad request
			}else{
				sb.append("email = '"+user.getEmail()+"', ");
			}
			
			if (user.getSexo() == null || user.getSexo() == ""){
				throw new WebApplicationException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(APIErrorBuilder.buildError(
								Response.Status.BAD_REQUEST
										.getStatusCode(),
								"Bad request. El sexo no puede ser null.", request)).build());//400=> bad request
			}else{
				sb.append("sexo = '"+user.getSexo()+"', ");
			}
			
			if (user.getCiudad() == null || user.getCiudad() == ""){
				throw new WebApplicationException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(APIErrorBuilder.buildError(
								Response.Status.BAD_REQUEST
										.getStatusCode(),
								"Bad request. La ciudad no puede ser null.", request)).build());//400=> bad request
			}else{
				sb.append("ciudad = '"+user.getCiudad()+"', ");
			}	
			
			
			if (user.getNacionalidad() == "" || user.getNacionalidad() == null) {
				sb.append("nacionalidad = NULL, ");
			} else {
				sb.append("nacionalidad = '" + user.getNacionalidad() + "', ");
			}
			if (user.getEstado_civil() == "" || user.getEstado_civil() == null) {
				sb.append("estado_civil = NULL, ");
			} else {
				sb.append("estado_civil = '" + user.getEstado_civil() + "', ");
			}
			if (user.getPelo() == "" || user.getPelo() == null) {
				sb.append("pelo = NULL, ");
			} else {
				sb.append("pelo = '" + user.getPelo() + "', ");
			}
			if (user.getOjos() == "" || user.getOjos() == null) {
				sb.append("ojos = NULL, ");
			} else {
				sb.append("ojos = '" + user.getOjos() + "', ");
			}
			if (user.getEstatura() == "" || user.getEstatura() == null) {
				sb.append("estatura = NULL, ");
			} else {
				sb.append("estatura = '" + user.getEstatura() + "', ");
			}			
			if (user.getComplexion() == "" || user.getComplexion() == null) {
				sb.append("complexion = NULL, ");
			} else {
				sb.append("complexion = '" + user.getComplexion() + "', ");
			}
			if (user.getEmpleo() == "" || user.getEmpleo() == null) {
				sb.append("empleo = NULL, ");
			} else {
				sb.append("empleo = '" + user.getEmpleo() + "', ");
			}
			if (user.getHijos() == "" || user.getHijos() == null) {
				sb.append("hijos = NULL, ");
			} else {
				sb.append("hijos = '" + user.getHijos() + "', ");
			}			
			if (user.getDescripcion() == "" || user.getDescripcion() == null) {
				sb.append("descripcion = NULL, ");
			} else {
				sb.append("descripcion = '" + user.getDescripcion() + "', ");
			}
			if (user.getPreferencias() == "" || user.getPreferencias() == null) {
				sb.append("preferencias = NULL ");
			} else {
				sb.append("preferencias = '" + user.getPreferencias() + "' ");
			}
			
			sb.append(" where user = '"+username+"'");
			
			int i = stmt.executeUpdate(sb.toString());
			if (i==0){
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"User not found.", request)).build());
			}
			
			stmt.close();
			connection.close();
			
			System.out.println("Comandos update:");
			System.out.println(sb);
			System.out.println("Update realizado correctamente.");
			
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}	
	}
	
	
	
}
