package dsa.tasting.rest.model;

public class User {

	// - - - - - FIJAROS!
	//para ahorrarnos un huevo de lios le he puesto a los atributos de la clase user el mismo
	//nombre que presentan en la base de datos, así no hay problemas de nomenclatura ni mierdas
	private int id_user;
	private String user;
	private String clave;
	private String nombre;
	private String fecha;//pese a ser un número, como no hacemos nada con el, lo tratamos como String
	private String telefono;
	private String email;
	private String sexo;
	private String ciudad;
	private String nacionalidad;
	private String estado_civil;
	private String pelo;
	private String ojos;
	private String estatura;//valores: Alta, Baja, Media: NO NÚMEROS
	private String complexion;
	private String empleo;
	private String hijos;//pese a ser un número, como no hacemos nada con el, lo tratamos como String
	private String descripcion;
	private String preferencias;
	
	public int getId_user() {
		return id_user;
	}
	public void setId_user(int id_user) {
		this.id_user = id_user;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getCiudad() {
		return ciudad;
	}
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}
	public String getNacionalidad() {
		return nacionalidad;
	}
	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}
	public String getEstado_civil() {
		return estado_civil;
	}
	public void setEstado_civil(String estado_civil) {
		this.estado_civil = estado_civil;
	}
	public String getPelo() {
		return pelo;
	}
	public void setPelo(String pelo) {
		this.pelo = pelo;
	}
	public String getOjos() {
		return ojos;
	}
	public void setOjos(String ojos) {
		this.ojos = ojos;
	}
	public String getEstatura() {
		return estatura;
	}
	public void setEstatura(String estatura) {
		this.estatura = estatura;
	}
	public String getComplexion() {
		return complexion;
	}
	public void setComplexion(String complexion) {
		this.complexion = complexion;
	}
	public String getEmpleo() {
		return empleo;
	}
	public void setEmpleo(String empleo) {
		this.empleo = empleo;
	}
	public String getHijos() {
		return hijos;
	}
	public void setHijos(String hijos) {
		this.hijos = hijos;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getPreferencias() {
		return preferencias;
	}
	public void setPreferencias(String preferencias) {
		this.preferencias = preferencias;
	}


}
