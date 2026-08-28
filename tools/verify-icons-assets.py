from pathlib import Path
from PIL import Image

PROJECT_ROOT = Path(__file__).resolve().parent.parent

DRAWABLE_NODPI = (
    PROJECT_ROOT
    / "app"
    / "src"
    / "main"
    / "res"
    / "drawable-nodpi"
)

# ============================================================
# RECURSOS ESPERADOS
# ============================================================

# Marcos de perfil
# Requisitos:
# - PNG
# - 512 x 512 px
# - Fondo transparente
# - Centro visualmente libre para mostrar el numero del nivel
FRAME_SPECS = {
    "profile_frame_initial.png": (512, 512),
    "profile_frame_advanced_1.png": (512, 512),
    "profile_frame_advanced_2.png": (512, 512),
    "profile_frame_master.png": (512, 512),
    "profile_frame_elite.png": (512, 512),
    "profile_frame_legend.png": (512, 512),
}

# Medallas
# Requisitos:
# - PNG
# - 512 x 512 px
# - Fondo transparente
MEDAL_SPECS = {
    "medal_bronze.png": (512, 512),
    "medal_silver.png": (512, 512),
    "medal_gold.png": (512, 512),
    "medal_platinum.png": (512, 512),
    "medal_diamond.png": (512, 512),
    "medal_legend.png": (512, 512),
}

# Logros e insignias
# Requisitos:
# - PNG
# - 512 x 512 px
# - Fondo transparente
ACHIEVEMENT_SPECS = {
    "achievement_first_step.png": (512, 512),
    "achievement_ascenso.png": (512, 512),
    "achievement_avanzado.png": (512, 512),
    "achievement_desafio_superado.png": (512, 512),
    "achievement_maestro.png": (512, 512),
    "achievement_first_legend.png": (512, 512),
    "achievement_grand_master.png": (512, 512),
    "achievement_elite_sudolux.png": (512, 512),
    "achievement_legend_sudolux.png": (512, 512),
    "absolute_mastery_1.png": (512, 512),
    "absolute_mastery_5.png": (512, 512),
    "absolute_mastery_10.png": (512, 512),
    "absolute_mastery_25.png": (512, 512),
}

# Tableros especiales
# Requisitos:
# - PNG
# - 1024 x 1024 px
#
# El tablero "Predeterminado del tema" no necesita recurso nuevo.
BOARD_SPECS = {
    "board_alternative.png": (1024, 1024),
    "board_advanced.png": (1024, 1024),
    "board_expert.png": (1024, 1024),
    "board_grand_master.png": (1024, 1024),
    "board_exclusive.png": (1024, 1024),
}

errors = []
warnings = []
ok_count = 0


# ============================================================
# FUNCIONES DE VERIFICACION
# ============================================================

def check_png(
    path: Path,
    expected_size: tuple[int, int],
    require_transparency: bool = False,
):
    global ok_count

    if not path.exists():
        errors.append(f"FALTA: {path.name}")
        return False

    if not path.is_file():
        errors.append(f"NO ES ARCHIVO: {path.name}")
        return False

    try:
        with Image.open(path) as img:
            if img.format != "PNG":
                errors.append(
                    f"FORMATO INCORRECTO: {path.name} "
                    f"-> {img.format} (debe ser PNG)"
                )
                return False

            actual_size = img.size

            if actual_size != expected_size:
                errors.append(
                    f"DIMENSION INCORRECTA: {path.name} "
                    f"-> {actual_size[0]}x{actual_size[1]} "
                    f"(debe ser {expected_size[0]}x{expected_size[1]})"
                )
                return False

            if require_transparency:
                rgba = img.convert("RGBA")
                alpha = rgba.getchannel("A")
                alpha_min, alpha_max = alpha.getextrema()

                if alpha_min == 255 and alpha_max == 255:
                    errors.append(
                        f"SIN TRANSPARENCIA: {path.name}"
                    )
                    return False

        print(
            f"OK  {path.name:<42} "
            f"{expected_size[0]}x{expected_size[1]}"
        )

        ok_count += 1
        return True

    except Exception as exc:
        errors.append(
            f"NO SE PUEDE LEER: {path.name} -> {exc}"
        )
        return False


def check_frame_center_transparency(path: Path):
    """
    Verifica que el centro visual del marco quede suficientemente
    libre para mostrar el numero del nivel.

    No exige transparencia absoluta en una zona excesivamente
    amplia, porque puede existir antialiasing, sombras suaves
    o pequeños brillos cercanos al borde interior.
    """

    if not path.exists():
        return

    try:
        with Image.open(path) as source:
            img = source.convert("RGBA")

            width, height = img.size

            # Se comprueba solo el 20 % central del lienzo.
            # En una imagen de 512x512 representa aproximadamente
            # una zona de 102x102 px en el centro.
            left = int(width * 0.40)
            top = int(height * 0.40)
            right = int(width * 0.60)
            bottom = int(height * 0.60)

            center = img.crop(
                (left, top, right, bottom)
            )

            alpha = center.getchannel("A")

            pixels = list(alpha.getdata())
            total_pixels = len(pixels)

            if total_pixels == 0:
                errors.append(
                    f"NO SE PUDO ANALIZAR CENTRO: {path.name}"
                )
                return

            # Solo se consideran realmente visibles los pixeles
            # con alpha superior a 20.
            visible_pixels = sum(
                1 for value in pixels if value > 20
            )

            visible_percentage = (
                visible_pixels / total_pixels
            ) * 100

            # Se permite hasta 1 % residual por antialiasing,
            # sombra o brillos leves.
            if visible_percentage > 1.0:
                errors.append(
                    f"CENTRO OCUPADO: {path.name} "
                    f"-> {visible_percentage:.2f}% del area central "
                    f"contiene pixeles visibles"
                )
            else:
                print(
                    f"    Centro libre: OK "
                    f"({visible_percentage:.2f}% residual)"
                )

    except Exception as exc:
        errors.append(
            f"NO SE PUDO COMPROBAR CENTRO: "
            f"{path.name} -> {exc}"
        )


def check_android_resource_name(path: Path):
    name = path.name

    if " " in name:
        errors.append(
            f"NOMBRE CON ESPACIOS: {name}"
        )

    if any(char.isupper() for char in name):
        errors.append(
            f"NOMBRE CON MAYUSCULAS: {name}"
        )

    if name.lower().endswith(".png.png"):
        errors.append(
            f"DOBLE EXTENSION: {name}"
        )

    stem = path.stem

    allowed = set(
        "abcdefghijklmnopqrstuvwxyz"
        "0123456789_"
    )

    invalid_chars = sorted(
        {char for char in stem if char not in allowed}
    )

    if invalid_chars:
        errors.append(
            f"NOMBRE ANDROID INVALIDO: {name} "
            f"-> caracteres no permitidos: "
            f"{''.join(invalid_chars)}"
        )


# ============================================================
# INICIO
# ============================================================

print()
print("======================================================")
print(" VERIFICACION DE ASSETS DE LOGROS - SUDOLUX")
print("======================================================")
print()

print("Carpeta revisada:")
print(DRAWABLE_NODPI)
print()

if not DRAWABLE_NODPI.exists():
    print("ERROR: No existe la carpeta:")
    print(DRAWABLE_NODPI)
    raise SystemExit(1)


# ============================================================
# 1. MARCOS DE PERFIL
# ============================================================

print("MARCOS DE PERFIL")
print("-" * 54)

for filename, size in FRAME_SPECS.items():
    path = DRAWABLE_NODPI / filename

    valid = check_png(
        path,
        expected_size=size,
        require_transparency=True,
    )

    if valid:
        check_frame_center_transparency(path)

print()


# ============================================================
# 2. MEDALLAS
# ============================================================

print("MEDALLAS")
print("-" * 54)

for filename, size in MEDAL_SPECS.items():
    check_png(
        DRAWABLE_NODPI / filename,
        expected_size=size,
        require_transparency=True,
    )

print()


# ============================================================
# 3. LOGROS E INSIGNIAS
# ============================================================

print("LOGROS E INSIGNIAS")
print("-" * 54)

for filename, size in ACHIEVEMENT_SPECS.items():
    check_png(
        DRAWABLE_NODPI / filename,
        expected_size=size,
        require_transparency=True,
    )

print()


# ============================================================
# 4. TABLEROS ESPECIALES
# ============================================================

print("TABLEROS ESPECIALES")
print("-" * 54)

for filename, size in BOARD_SPECS.items():
    check_png(
        DRAWABLE_NODPI / filename,
        expected_size=size,
        require_transparency=False,
    )

print()


# ============================================================
# 5. COMPROBACION DE NOMBRES
# ============================================================

print("COMPROBACION DE NOMBRES")
print("-" * 54)

expected_files = (
    set(FRAME_SPECS)
    | set(MEDAL_SPECS)
    | set(ACHIEVEMENT_SPECS)
    | set(BOARD_SPECS)
)

related_prefixes = (
    "profile_frame_",
    "medal_",
    "achievement_",
    "absolute_mastery_",
    "board_",
)

for path in DRAWABLE_NODPI.iterdir():
    if not path.is_file():
        continue

    name_lower = path.name.lower()

    if (
        path.name.startswith(related_prefixes)
        or "chatgpt image" in name_lower
    ):
        check_android_resource_name(path)

        if (
            path.suffix.lower() == ".png"
            and path.name not in expected_files
        ):
            warnings.append(
                f"PNG NO CONTEMPLADO: {path.name}"
            )

print("Comprobacion de nombres finalizada.")
print()


# ============================================================
# 6. RESUMEN
# ============================================================

frame_total = len(FRAME_SPECS)
medal_total = len(MEDAL_SPECS)
achievement_total = len(ACHIEVEMENT_SPECS)
board_total = len(BOARD_SPECS)

expected_total = (
    frame_total
    + medal_total
    + achievement_total
    + board_total
)

print("======================================================")
print(" RESUMEN")
print("======================================================")
print()

print(f"Marcos esperados       : {frame_total}")
print(f"Medallas esperadas     : {medal_total}")
print(f"Logros esperados       : {achievement_total}")
print(f"Tableros esperados     : {board_total}")
print("-" * 54)
print(f"Archivos esperados     : {expected_total}")
print(f"Archivos correctos     : {ok_count}")
print(f"Errores encontrados    : {len(errors)}")
print(f"Advertencias           : {len(warnings)}")


# ============================================================
# 7. ADVERTENCIAS
# ============================================================

if warnings:
    print()
    print("ADVERTENCIAS")
    print("-" * 54)

    for warning in warnings:
        print(f"AVISO: {warning}")


# ============================================================
# 8. RESULTADO FINAL
# ============================================================

if errors:
    print()
    print("RESULTADO: SE ENCONTRARON PROBLEMAS")
    print()

    for error in errors:
        print(f"ERROR: {error}")

    print()
    print("Los assets todavia no estan listos para integracion.")
    print("======================================================")
    print()

    raise SystemExit(1)

else:
    print()
    print("RESULTADO: TODO CORRECTO")
    print()
    print(
        "Los recursos contemplados para Progreso > Logros "
        "cumplen los requisitos definidos."
    )
    print()
    print(
        "Marcos: 512x512 PNG transparente y centro visualmente libre."
    )
    print(
        "Medallas: 512x512 PNG transparente."
    )
    print(
        "Logros: 512x512 PNG transparente."
    )
    print(
        "Tableros: 1024x1024 PNG."
    )
    print()
    print("Carpeta validada:")
    print(
        "app/src/main/res/drawable-nodpi/"
    )
    print()
    print(
        f"Total validado: {expected_total} recursos."
    )
    print("======================================================")
    print()