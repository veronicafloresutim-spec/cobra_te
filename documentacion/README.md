# Documentación del Proyecto Cobra Te

Esta carpeta contiene la documentación técnica oficial del Sistema POS Cobra Te, incluyendo el Acta de Alcance y la Especificación de Requerimientos.

## Documentos Incluidos

### 1. **Acta de Alcance** (`acta_de_alcance.tex`)
- **Propósito**: Define el alcance completo del proyecto, objetivos, entregables y cronograma
- **Basado en**: Layout SACI_AlcanceProyecto
- **Cobertura**: Tres fases - Desktop, Web, y Móvil
- **Estado**: Versión 1.5 - Aprobado

### 2. **Especificación de Requerimientos** (`especificacion_requerimientos.tex`)  
- **Propósito**: Detalla todos los requerimientos funcionales y no funcionales del sistema
- **Estándar**: IEEE 830 para Software Requirements Specification (SRS)
- **Cobertura**: Requerimientos completos para plataforma multiplataforma
- **Estado**: Versión 1.6 - En Desarrollo

## Características de la Documentación

### 🎨 **Diseño Consistente**
- **Logotipo oficial**: `Logo.png` integrado en encabezados y portadas
- **Membrete unificado** entre ambos documentos con branding corporativo
- **Colores corporativos** de Cobra Te:
  - Azul principal: `#6750A4` 
  - Naranja secundario: `#FF6B35`
  - Verde éxito: `#4CAF50`
  - Gris corporativo: `#49454F`

### 📋 **Histórico de Cambios**
Ambos documentos incluyen tabla de seguimiento con:
- **Fecha de revisión**
- **Versión del documento** 
- **Responsable(s)**: Verónica Flores (Directora del Proyecto)
- **Descripción de cambios**

### 🌐 **Enfoque Multiplataforma**
La documentación contempla la evolución del proyecto en 3 fases:
1. **Fase 1**: Aplicación de escritorio JavaFX (actual)
2. **Fase 2**: Portal web React.js y API REST integrada
3. **Fase 3**: Aplicación móvil React Native multiplataforma

## Compilación de Documentos LaTeX

### Prerrequisitos
```bash
# Instalar distribución LaTeX completa
# Windows: MiKTeX o TeX Live
# macOS: MacTeX  
# Linux: texlive-full

# Paquetes requeridos:
- inputenc, babel, geometry
- fancyhdr, graphicx, longtable
- xcolor, hyperref, booktabs
- multirow, colortbl, enumitem
```

### Compilación

#### Método 1: Línea de Comandos
```bash
# Navegar a la carpeta de documentación
cd documentacion/

# Compilar Acta de Alcance
pdflatex acta_de_alcance.tex
pdflatex acta_de_alcance.tex  # Segunda pasada para referencias

# Compilar Especificación de Requerimientos  
pdflatex especificacion_requerimientos.tex
pdflatex especificacion_requerimientos.tex  # Segunda pasada para referencias
```

#### Método 2: Editor LaTeX
- **Recomendado**: TeXstudio, Overleaf, VS Code con LaTeX Workshop
- Abrir archivos `.tex` y compilar con `F5` o botón de compilación
- Asegurar que el compilador esté configurado para `pdflatex`

#### Método 3: Docker (Alternativo)
```bash
# Usar imagen LaTeX oficial
docker run --rm -v $(pwd):/workdir texlive/texlive:latest pdflatex acta_de_alcance.tex
docker run --rm -v $(pwd):/workdir texlive/texlive:latest pdflatex especificacion_requerimientos.tex
```

### Archivos Generados
Después de la compilación exitosa:
- `acta_de_alcance.pdf` - Documento final del Acta de Alcance
- `especificacion_requerimientos.pdf` - Documento final de Requerimientos
- Archivos auxiliares (`.aux`, `.log`, `.toc`) - Pueden eliminarse

## Estructura del Contenido

### Acta de Alcance
1. **Introducción** - Propósito y antecedentes
2. **Descripción del Proyecto** - Visión y objetivos
3. **Alcance por Fases** - Desktop, Web, Móvil
4. **Entregables** - Por cada fase de desarrollo
5. **Arquitectura Tecnológica** - Tecnologías actuales y futuras
6. **Restricciones y Limitaciones** - Técnicas y de negocio
7. **Criterios de Éxito** - Métricas por fase
8. **Gestión de Riesgos** - Identificación y mitigación
9. **Cronograma** - Duración estimada por fase

### Especificación de Requerimientos
1. **Introducción** - Propósito y alcance del SRS
2. **Descripción General** - Perspectiva y funciones del producto
3. **Requerimientos Específicos** - Funcionales por fase
4. **Requerimientos No Funcionales** - Performance, seguridad, usabilidad
5. **Casos de Uso** - Flujos principales del sistema
6. **Interfaces Externas** - Usuario, hardware, software
7. **Matriz de Trazabilidad** - Mapeo requerimientos-casos de uso
8. **Plan de Pruebas** - Estrategia y criterios de aceptación
9. **Consideraciones de Implementación** - Fases y riesgos

## Mantenimiento y Actualizaciones

### Control de Versiones
- **Formato de versión**: `X.Y` (Mayor.Menor)
- **Responsable de actualizaciones**: Verónica Flores
- **Frecuencia de revisión**: Mensual o por hitos del proyecto

### Proceso de Actualización
1. Modificar archivo `.tex` correspondiente
2. Actualizar tabla de "Histórico de Cambios"
3. Incrementar número de versión en portada
4. Recompilar documento
5. Validar PDF generado
6. Distribuir a stakeholders del proyecto

### Archivos de Respaldo
Se recomienda mantener:
- Versiones anteriores de archivos `.tex`
- PDFs compilados de cada versión
- Control de versiones con Git para seguimiento de cambios

## Contacto y Soporte

**Responsable del Proyecto**: Verónica Flores  
**Email**: [correo de contacto]  
**Proyecto**: Sistema POS Cobra Te  
**Repositorio**: `cobra_te`  

---

*Documentación actualizada el 3 de octubre de 2025*  
*Cobra Te - Sistema de Punto de Venta Integral*  
*Versión 1.6 - Layout profesional optimizado*