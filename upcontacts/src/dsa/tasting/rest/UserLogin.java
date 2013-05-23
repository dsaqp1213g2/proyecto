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
import javax.ws.rs.POST;
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

@Path("/login")
public class UserLogin {

	@Context
	HttpServletRequest request;

	@Context
	private SecurityContext security;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User createUserJSON(User user) {
		loginUser(user);
		System.out.println("lallalalalala"+user.getUser());
		return getUser(user.getUser());
		
		/*Response response = null;
		try {
			response = Response.status(204)
					.location(new URI("/users" + user.getUser())).build();
		} catch (Exception e) {
		}

		return response;*/
		
	}

	private void loginUser(User user) {
		Connection connection = null;
		try {
			connection = DataSourceSAP.getInstance().getDataSource()
					.getConnection();
		} catch (SQLException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.SERVICE_UNAVAILABLE)
							.entity(APIErrorBuilder.buildError(
									Response.Status.SERVICE_UNAVAILABLE
											.getStatusCode(),
									"Service unavailable.", request)).build());
		}

		try {
			// si alguno de los par�metros obligatorios es null se devuelve un
			// Bad request donde te lo pone claro
			System.out.println("login user post: "+user.getUser());
			System.out.println("login clave post: "+user.getClave());
			
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("select * from usuarios where user='"+user.getUser()+"' and clave=SHA1('"+user.getClave()+"')");
				rs.next();
				if (rs.getString("user")==null){
					throw new WebApplicationException(Response
							.status(Response.Status.NOT_FOUND)
							.entity(APIErrorBuilder.buildError(
									Response.Status.NOT_FOUND.getStatusCode(),
									"Error, pruebe otra vez", request)).build());
				}
				
			/*	user.setId_user(rs.getInt("id_user"));
				user.setUser(rs.getString("user"));
				user.setNombre(rs.getString("nombre"));
				user.setTelefono(rs.getString("telefono"));
				user.setEmail(rs.getString("email"));
				user.setSexo(rs.getString("sexo"));
				user.setFecha(rs.getString("fecha"));
				user.setCiudad(rs.getString("ciudad"));
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

				System.out.println("ya esta, existe el usuario: " + user.getUser());*/
			
		} catch (Exception e) {

			try {
				connection.rollback();
			} catch (Exception e1) {
			}

			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		} finally {

			try {
				connection.setAutoCommit(true);
				connection.close();
			} catch (Exception e2) {
			}
		}

	
		
	

	}

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
		user.setClave(rs.getString("clave"));
		user.setNombre(rs.getString("nombre"));
		user.setTelefono(rs.getString("telefono"));
		user.setEmail(rs.getString("email"));
		user.setSexo(rs.getString("sexo"));
		user.setFecha(rs.getString("fecha"));
		user.setCiudad(rs.getString("ciudad"));
		// no obligatorios: los dem�s
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

}