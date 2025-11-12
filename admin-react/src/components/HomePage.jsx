import './HomePage.css';

export default function HomePage() {
  return (
    <div className="home-page">
      <div className="hero-section">
        <div className="logo-container">
          <img src="/cliente/img/LOGO.png" alt="Level-Up Gamer Logo" className="main-logo" />
          <h1>LEVEL<span className="up">UP</span> <span className="gamer">GAMER</span></h1>
          <p>Tu tienda gamer de confianza</p>
        </div>
        
        <div className="access-options">
          <div className="option-card">
            <h2>🛍️ Tienda Online</h2>
            <p>Explora nuestro catálogo de productos gaming</p>
            <a href="/cliente/" className="btn btn-primary">
              Ir a la Tienda
            </a>
          </div>
          
          <div className="option-card">
            <h2>⚙️ Panel de Administración</h2>
            <p>Acceso para administradores y vendedores</p>
            <a href="/admin" className="btn btn-secondary">
              Acceder al Panel
            </a>
          </div>
        </div>
      </div>
    </div>
  );
}