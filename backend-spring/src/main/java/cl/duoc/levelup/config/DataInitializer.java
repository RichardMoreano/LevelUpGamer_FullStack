package cl.duoc.levelup.config;

import cl.duoc.levelup.entity.Producto;
import cl.duoc.levelup.entity.Usuario;
import cl.duoc.levelup.repository.ProductoRepository;
import cl.duoc.levelup.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        initializeProducts();
    }

    private void initializeUsers() {
        // Verificar si ya existen usuarios
        if (usuarioRepository.count() > 0) {
            return;
        }

        System.out.println("🔧 Inicializando usuarios por defecto...");

        // Usuario Administrador
        Usuario admin = new Usuario();
        admin.setRun("12345678-9");
        admin.setNombres("Administrador");
        admin.setApellidos("Sistema");
        admin.setCorreo("admin@levelup.cl");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setTipoUsuario(Usuario.TipoUsuario.ADMIN);
        admin.setRegion("Metropolitana");
        admin.setComuna("Santiago");
        admin.setDireccion("Av. Principal 123");
        admin.setFechaRegistro(LocalDateTime.now());
        admin.setActivo(true);
        admin.setPuntosLevelUp(0);
        usuarioRepository.save(admin);

        // Usuario Vendedor
        Usuario vendedor = new Usuario();
        vendedor.setRun("98765432-1");
        vendedor.setNombres("Juan Carlos");
        vendedor.setApellidos("Vendedor");
        vendedor.setCorreo("vendedor@levelup.cl");
        vendedor.setPassword(passwordEncoder.encode("vendedor123"));
        vendedor.setTipoUsuario(Usuario.TipoUsuario.VENDEDOR);
        vendedor.setRegion("Metropolitana");
        vendedor.setComuna("Las Condes");
        vendedor.setDireccion("Av. Apoquindo 456");
        vendedor.setFechaRegistro(LocalDateTime.now());
        vendedor.setActivo(true);
        vendedor.setPuntosLevelUp(0);
        usuarioRepository.save(vendedor);

        // Usuario Cliente DUOC
        Usuario clienteDuoc = new Usuario();
        clienteDuoc.setRun("11111111-1");
        clienteDuoc.setNombres("María José");
        clienteDuoc.setApellidos("Estudiante DUOC");
        clienteDuoc.setCorreo("maria.estudiante@duocuc.cl");
        clienteDuoc.setPassword(passwordEncoder.encode("duoc123"));
        clienteDuoc.setTipoUsuario(Usuario.TipoUsuario.CLIENTE);
        clienteDuoc.setRegion("Metropolitana");
        clienteDuoc.setComuna("Maipú");
        clienteDuoc.setDireccion("Av. Bernardo O'Higgins 789");
        clienteDuoc.setFechaRegistro(LocalDateTime.now());
        clienteDuoc.setActivo(true);
        clienteDuoc.setPuntosLevelUp(150); // Cliente con puntos
        usuarioRepository.save(clienteDuoc);

        // Usuario Cliente Normal
        Usuario clienteNormal = new Usuario();
        clienteNormal.setRun("22222222-2");
        clienteNormal.setNombres("Pedro Antonio");
        clienteNormal.setApellidos("Cliente Normal");
        clienteNormal.setCorreo("pedro.cliente@gmail.com");
        clienteNormal.setPassword(passwordEncoder.encode("cliente123"));
        clienteNormal.setTipoUsuario(Usuario.TipoUsuario.CLIENTE);
        clienteNormal.setRegion("Valparaíso");
        clienteNormal.setComuna("Viña del Mar");
        clienteNormal.setDireccion("Calle Falsa 321");
        clienteNormal.setFechaRegistro(LocalDateTime.now());
        clienteNormal.setActivo(true);
        clienteNormal.setPuntosLevelUp(50);
        usuarioRepository.save(clienteNormal);

        System.out.println("✅ Usuarios inicializados correctamente");
    }

    private void initializeProducts() {
        // Verificar si ya existen productos
        if (productoRepository.count() > 0) {
            return;
        }

        System.out.println("🎮 Inicializando productos por defecto...");

        // Consolas
        Producto ps5 = new Producto();
        ps5.setCodigo("CON001");
        ps5.setNombre("PlayStation 5");
        ps5.setCategoria("Consolas");
        ps5.setDescripcion("Consola de videojuegos de última generación de Sony");
        ps5.setPrecio(new BigDecimal("599990"));
        ps5.setStock(10);
        ps5.setStockCritico(5);
        ps5.setImagen("ps5.jpg");
        ps5.setActivo(true);
        productoRepository.save(ps5);

        Producto xbox = new Producto();
        xbox.setCodigo("CON002");
        xbox.setNombre("Xbox Series X");
        xbox.setCategoria("Consolas");
        xbox.setDescripcion("La consola Xbox más potente de la historia");
        xbox.setPrecio(new BigDecimal("549990"));
        xbox.setStock(8);
        xbox.setStockCritico(3);
        xbox.setImagen("xbox-series-x.jpg");
        xbox.setActivo(true);
        productoRepository.save(xbox);

        Producto nintendo = new Producto();
        nintendo.setCodigo("CON003");
        nintendo.setNombre("Nintendo Switch");
        nintendo.setCategoria("Consolas");
        nintendo.setDescripcion("Consola híbrida portátil y de sobremesa");
        nintendo.setPrecio(new BigDecimal("299990"));
        nintendo.setStock(15);
        nintendo.setStockCritico(5);
        nintendo.setImagen("nintendo-switch.jpg");
        nintendo.setActivo(true);
        productoRepository.save(nintendo);

        // Juegos PS5
        Producto spiderMan = new Producto();
        spiderMan.setCodigo("JUE001");
        spiderMan.setNombre("Marvel's Spider-Man 2");
        spiderMan.setCategoria("Juegos PS5");
        spiderMan.setDescripcion("La secuela del exitoso juego de Spider-Man");
        spiderMan.setPrecio(new BigDecimal("69990"));
        spiderMan.setStock(25);
        spiderMan.setStockCritico(10);
        spiderMan.setImagen("spiderman-2.jpg");
        spiderMan.setActivo(true);
        productoRepository.save(spiderMan);

        Producto godOfWar = new Producto();
        godOfWar.setCodigo("JUE002");
        godOfWar.setNombre("God of War Ragnarök");
        godOfWar.setCategoria("Juegos PS5");
        godOfWar.setDescripcion("La épica conclusión de la saga nórdica");
        godOfWar.setPrecio(new BigDecimal("59990"));
        godOfWar.setStock(20);
        godOfWar.setStockCritico(8);
        godOfWar.setImagen("god-of-war-ragnarok.jpg");
        godOfWar.setActivo(true);
        productoRepository.save(godOfWar);

        // Juegos Xbox
        Producto halo = new Producto();
        halo.setCodigo("JUE003");
        halo.setNombre("Halo Infinite");
        halo.setCategoria("Juegos Xbox");
        halo.setDescripcion("El regreso del Master Chief");
        halo.setPrecio(new BigDecimal("49990"));
        halo.setStock(18);
        halo.setStockCritico(6);
        halo.setImagen("halo-infinite.jpg");
        halo.setActivo(true);
        productoRepository.save(halo);

        // Juegos Nintendo
        Producto mario = new Producto();
        mario.setCodigo("JUE004");
        mario.setNombre("Super Mario Odyssey");
        mario.setCategoria("Juegos Nintendo");
        mario.setDescripcion("Una gran aventura de Mario en 3D");
        mario.setPrecio(new BigDecimal("59990"));
        mario.setStock(22);
        mario.setStockCritico(8);
        mario.setImagen("mario-odyssey.jpg");
        mario.setActivo(true);
        productoRepository.save(mario);

        // Accesorios
        Producto controlPs5 = new Producto();
        controlPs5.setCodigo("ACC001");
        controlPs5.setNombre("DualSense Controller");
        controlPs5.setCategoria("Accesorios");
        controlPs5.setDescripcion("Control inalámbrico para PlayStation 5");
        controlPs5.setPrecio(new BigDecimal("79990"));
        controlPs5.setStock(30);
        controlPs5.setStockCritico(15);
        controlPs5.setImagen("dualsense-controller.jpg");
        controlPs5.setActivo(true);
        productoRepository.save(controlPs5);

        Producto headset = new Producto();
        headset.setCodigo("ACC002");
        headset.setNombre("Gaming Headset RGB");
        headset.setCategoria("Accesorios");
        headset.setDescripcion("Auriculares gamer con micrófono y RGB");
        headset.setPrecio(new BigDecimal("39990"));
        headset.setStock(40);
        headset.setStockCritico(20);
        headset.setImagen("gaming-headset.jpg");
        headset.setActivo(true);
        productoRepository.save(headset);

        // Producto con stock crítico para testing
        Producto memoriaUsb = new Producto();
        memoriaUsb.setCodigo("ACC003");
        memoriaUsb.setNombre("Memoria USB Gaming 64GB");
        memoriaUsb.setCategoria("Accesorios");
        memoriaUsb.setDescripcion("Memoria USB de alta velocidad para gamers");
        memoriaUsb.setPrecio(new BigDecimal("15990"));
        memoriaUsb.setStock(2); // Stock crítico
        memoriaUsb.setStockCritico(5);
        memoriaUsb.setImagen("usb-gaming.jpg");
        memoriaUsb.setActivo(true);
        productoRepository.save(memoriaUsb);

        System.out.println("✅ Productos inicializados correctamente");
        System.out.println("📊 Total productos creados: " + productoRepository.count());
    }
}