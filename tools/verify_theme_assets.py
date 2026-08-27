from pathlib import Path
from PIL import Image

PROJECT_ROOT = Path(__file__).resolve().parent.parent

DRAWABLE = PROJECT_ROOT / "app" / "src" / "main" / "res" / "drawable"
DRAWABLE_NODPI = PROJECT_ROOT / "app" / "src" / "main" / "res" / "drawable-nodpi"

THEMES = ["ocean", "forest", "ambar", "master"]

# Fondos: nombre parcial -> dimensión obligatoria
BACKGROUND_SPECS = {
    "home": (1080, 1920),
    "game": (1080, 1920),
    "dialog": (1536, 1024),
    "popup": (1200, 800),
}

# Iconos: nombre parcial -> dimensión obligatoria
ICON_SPECS = {
    "difficulty_easy": (256, 256),
    "difficulty_medium": (256, 256),
    "difficulty_hard": (256, 256),
    "difficulty_expert": (256, 256),
    "difficulty_master": (256, 256),
    "difficulty_extreme": (256, 256),
    "badge_reward": (256, 256),
    "theme_active": (128, 128),
    "theme_icon": (256, 256),
    "theme_locked": (128, 128),
    "theme_thumbnail": (512, 512),
}

errors = []
ok_count = 0


def check_image(path: Path, expected_size):
    global ok_count

    if not path.exists():
        errors.append(f"FALTA: {path.name}")
        return

    try:
        with Image.open(path) as img:
            actual_size = img.size

            if actual_size != expected_size:
                errors.append(
                    f"DIMENSION INCORRECTA: {path.name} "
                    f"-> {actual_size[0]}x{actual_size[1]} "
                    f"(debe ser {expected_size[0]}x{expected_size[1]})"
                )
                return

        print(
            f"OK  {path.name:<45} "
            f"{expected_size[0]}x{expected_size[1]}"
        )
        ok_count += 1

    except Exception as exc:
        errors.append(f"NO SE PUEDE LEER: {path.name} -> {exc}")


print("\n==============================================")
print(" VERIFICACION DE RECURSOS DE TEMAS - SUDOLUX")
print("==============================================\n")

# 1. Fondos WEBP
print("FONDOS\n")

for theme in THEMES:
    for resource, size in BACKGROUND_SPECS.items():
        filename = f"theme_{theme}_bg_{resource}.webp"
        check_image(DRAWABLE_NODPI / filename, size)

# 2. Iconos PNG
print("\nICONOS\n")

for theme in THEMES:
    for resource, size in ICON_SPECS.items():
        filename = f"{theme}_{resource}.png"
        check_image(DRAWABLE / filename, size)

# 3. Detectar doble extensión
print("\nCOMPROBACION DE NOMBRES\n")

for folder in [DRAWABLE, DRAWABLE_NODPI]:
    if not folder.exists():
        errors.append(f"NO EXISTE LA CARPETA: {folder}")
        continue

    for path in folder.iterdir():
        name = path.name.lower()

        if ".webp.webp" in name:
            errors.append(f"DOBLE EXTENSION: {path.name}")

        if ".png.png" in name:
            errors.append(f"DOBLE EXTENSION: {path.name}")

        if " " in path.name:
            errors.append(f"NOMBRE CON ESPACIOS: {path.name}")

        if any(c.isupper() for c in path.name):
            errors.append(f"NOMBRE CON MAYUSCULAS: {path.name}")

# Resultado
print("\n==============================================")

expected_total = (
    len(THEMES) * len(BACKGROUND_SPECS)
    + len(THEMES) * len(ICON_SPECS)
)

print(f"Archivos esperados : {expected_total}")
print(f"Archivos correctos : {ok_count}")
print(f"Problemas encontrados: {len(errors)}")

if errors:
    print("\nRESULTADO: SE ENCONTRARON PROBLEMAS\n")

    for error in errors:
        print(f"ERROR: {error}")

    raise SystemExit(1)

else:
    print("\nRESULTADO: TODO CORRECTO")
    print("Los recursos de Ocean, Forest, Ambar y Master")
    print("tienen nombres, extensiones y dimensiones correctas.")

print("==============================================\n")