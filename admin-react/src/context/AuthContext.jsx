import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/apiService';

const AuthContext = createContext({});

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe ser usado dentro de un AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Cargar datos de autenticación al inicializar
  useEffect(() => {
    const initAuth = async () => {
      try {
        // Buscar tokens guardados
        const storedToken = localStorage.getItem('jwt_token') || localStorage.getItem('authToken');
        const storedUser = localStorage.getItem('usuario') || localStorage.getItem('sesion');

        console.log('🔍 AuthContext: Buscando tokens en localStorage...');
        console.log('Token encontrado:', !!storedToken);
        console.log('Usuario encontrado:', !!storedUser);

        if (storedToken && storedUser) {
          setToken(storedToken);
          
          // Parsear datos del usuario
          const userData = JSON.parse(storedUser);
          console.log('👤 Datos del usuario cargados:', userData);
          
          // Normalizar formato del usuario para React
          const normalizedUser = {
            email: userData.correo || userData.email,
            correo: userData.correo || userData.email,
            nombre: userData.nombres || userData.nombre,
            nombres: userData.nombres || userData.nombre,
            apellidos: userData.apellidos,
            run: userData.run,
            tipo: userData.tipoUsuario || userData.tipo,
            tipoUsuario: userData.tipoUsuario || userData.tipo,
            puntosLevelUp: userData.puntosLevelUp || 0,
            id: userData.run
          };
          
          setUser(normalizedUser);
          console.log('✅ Usuario normalizado para React:', normalizedUser);
          
          // Siempre autenticar si hay token y usuario
          setIsAuthenticated(true);
          console.log('✅ Usuario autenticado correctamente');
        } else {
          console.log('❌ No se encontraron credenciales válidas en localStorage');
        }
      } catch (error) {
        console.error('Error initializing auth:', error);
        logout();
      } finally {
        setIsLoading(false);
      }
    };

    initAuth();
  }, []);

  const login = async (email, password) => {
    try {
      setIsLoading(true);
      const response = await authAPI.login({ email, password });
      
      if (response && response.token) {
        const { token: authToken, refreshToken, usuario } = response;
        
        // Guardar en localStorage
        localStorage.setItem('authToken', authToken);
        localStorage.setItem('refreshToken', refreshToken);
        
        const userData = {
          email: usuario.correo,
          nombre: usuario.nombres,
          apellidos: usuario.apellidos,
          run: usuario.run,
          tipo: usuario.tipoUsuario,
          id: usuario.run
        };
        
        localStorage.setItem('sesion', JSON.stringify(userData));
        
        // Actualizar estado
        setToken(authToken);
        setUser(userData);
        setIsAuthenticated(true);
        
        return { success: true, user: userData };
      }
      
      throw new Error('Invalid response format');
    } catch (error) {
      console.error('Login error:', error);
      
      // Fallback para desarrollo local
      if (error.response?.status === 500 || !error.response) {
        return await loginFallback(email, password);
      }
      
      return { 
        success: false, 
        error: error.message || 'Error de autenticación' 
      };
    } finally {
      setIsLoading(false);
    }
  };

  const loginFallback = async (email, password) => {
    // Datos de prueba para desarrollo
    const testUsers = [
      {
        email: 'admin@levelup.cl',
        password: 'admin123',
        run: '12345678-9',
        nombres: 'Administrador',
        apellidos: 'Sistema',
        tipo: 'ADMIN'
      },
      {
        email: 'vendedor@levelup.cl', 
        password: 'vendedor123',
        run: '98765432-1',
        nombres: 'Vendedor',
        apellidos: 'Tienda',
        tipo: 'VENDEDOR'
      }
    ];

    const user = testUsers.find(u => u.email === email && u.password === password);
    
    if (user) {
      const userData = {
        email: user.email,
        nombre: user.nombres,
        apellidos: user.apellidos,
        run: user.run,
        tipo: user.tipo,
        id: user.run
      };
      
      const fakeToken = `fake-jwt-${Date.now()}`;
      
      localStorage.setItem('authToken', fakeToken);
      localStorage.setItem('sesion', JSON.stringify(userData));
      
      setToken(fakeToken);
      setUser(userData);
      setIsAuthenticated(true);
      
      return { success: true, user: userData };
    }
    
    return { success: false, error: 'Credenciales incorrectas' };
  };

  const register = async (userData) => {
    try {
      setIsLoading(true);
      const response = await authAPI.register(userData);
      
      if (response && response.success) {
        return { success: true, message: response.message };
      }
      
      throw new Error(response?.message || 'Error en registro');
    } catch (error) {
      console.error('Register error:', error);
      return { 
        success: false, 
        error: error.message || 'Error al registrar usuario' 
      };
    } finally {
      setIsLoading(false);
    }
  };

  const logout = async () => {
    try {
      // Llamar al endpoint de logout si existe token
      if (token) {
        await authAPI.logout();
      }
    } catch (error) {
      console.warn('Error during logout API call:', error);
    } finally {
      // Limpiar estado local
      localStorage.removeItem('authToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('sesion');
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('usuario');
      
      setToken(null);
      setUser(null);
      setIsAuthenticated(false);
      
      // Redirigir a la página principal del cliente
      window.location.href = '/cliente/index.html';
    }
  };

  const updateUser = (userData) => {
    const updatedUser = { ...user, ...userData };
    setUser(updatedUser);
    localStorage.setItem('sesion', JSON.stringify(updatedUser));
  };

  // Funciones de roles
  const isAdmin = () => user?.tipo === 'ADMIN';
  const isVendedor = () => user?.tipo === 'VENDEDOR';
  const isCliente = () => user?.tipo === 'CLIENTE';

  const value = {
    // Estado
    user,
    token,
    isAuthenticated,
    isLoading,
    
    // Funciones
    login,
    register,
    logout,
    updateUser,
    
    // Utilidades de roles
    isAdmin,
    isVendedor,
    isCliente,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;