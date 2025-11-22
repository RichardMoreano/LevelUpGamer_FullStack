package cl.duoc.levelup.dto;

public class RegistroUsuarioRequest {
    private String run;
    private String nombres;
    private String apellidos;
    private String correo;
    private String password;
    private String tipoUsuario;
    private String region;
    private String comuna;
    private String direccion;
    
    // Constructor por defecto
    public RegistroUsuarioRequest() {}
    
    // Getters y Setters
    public String getRun() { return run; }
    public void setRun(String run) { this.run = run; }
    
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}