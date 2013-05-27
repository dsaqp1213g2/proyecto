package dsa.tasting.rest;


import java.net.URI;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

import dsa.tasting.rest.model.Mensaje;
import dsa.tasting.rest.model.User;
import dsa.tasting.rest.util.APIErrorBuilder;
import dsa.tasting.rest.util.DataSourceSAP;


@Path("/buzonparticular/{username}")
public class BuzonParticular {
	
	@Context
	HttpServletRequest request;
	
	@Context
	private SecurityContext security;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Mensaje> getMensajesJSON(@PathParam("username") String username) {
		return getMensajes(username);
	}
	
	private List<Mensaje> getMensajes(String username) {// esta va de puta madre
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
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("select * from mensajes where receptor='"+username+"'");
			
			List<Mensaje> lista_mensajes = new ArrayList<>();
			while (rs.next()) {
				Mensaje mensaje = new Mensaje();
				// obligatorios: id_user, user, nombre, telefono, email, sexo,
				mensaje.setEmisor(rs.getString("emisor"));
				mensaje.setReceptor(rs.getString("receptor"));
				mensaje.setMensaje(rs.getString("mensaje"));
				System.out.println("while lista_mensajes:" +mensaje.getEmisor());
				lista_mensajes.add(mensaje);
			}

			stmt.close();
			connection.close();
			return lista_mensajes;
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
