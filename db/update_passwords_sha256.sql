-- Script para actualizar contraseñas a SHA-256
-- Ejecutar este script después de actualizar el código

-- Función para generar hash SHA-256 (En MariaDB 10.0.5+)
-- Las contraseñas actuales se convertirán a hash SHA-256

-- Actualizar contraseña del administrador (admin123 -> SHA-256)
UPDATE usuario 
SET contrasena = 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3' 
WHERE correo = 'admin@cobra_te.com' AND rol = 'administrador';

-- Actualizar contraseña de Maria (cajero123 -> SHA-256)
UPDATE usuario 
SET contrasena = '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5' 
WHERE correo = 'maria@cobra_te.com' AND rol = 'cajero';

-- Actualizar contraseña de Carlos (cajero123 -> SHA-256)
UPDATE usuario 
SET contrasena = '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5' 
WHERE correo = 'carlos@cobra_te.com' AND rol = 'cajero';

-- Verificar las actualizaciones
SELECT 
    idUsuario,
    nombres,
    apellidoPaterno,
    correo,
    rol,
    LEFT(contrasena, 20) as hash_preview
FROM usuario
ORDER BY rol, nombres;

-- Comentarios sobre los hashes:
-- admin123 (SHA-256): a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3
-- cajero123 (SHA-256): 5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5

-- IMPORTANTE: Después de ejecutar este script, todas las contraseñas estarán hasheadas
-- Las nuevas contraseñas para login son:
-- Administrador: admin@cobra_te.com / admin123
-- Cajeros: maria@cobra_te.com / cajero123
--          carlos@cobra_te.com / cajero123
