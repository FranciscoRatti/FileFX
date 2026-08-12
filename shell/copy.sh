#!/bin/bash

# Uso: ./merge-css-smart.sh <archivo_origen> <archivo_destino>

ORIGEN="${1:?Especifica archivo origen}"
DESTINO="${2:?Especifica archivo destino}"

if [[ ! -f "$ORIGEN" ]]; then
  echo -e "[\e[31mERROR\e[0m]    Error: \e[31m$ORIGEN\e[0m no existe"
  exit 1
fi

if [[ ! -f "$DESTINO" ]]; then
  echo -e "[\e[33mEXEC\e[0m]    Copiando \e[33m$ORIGEN\e[0m a \e[33m$DESTINO\e[0m"
  mkdir -p "$(dirname "$DESTINO")"
  cp "$ORIGEN" "$DESTINO"
  exit 0
fi

echo -e "[\e[33mEXEC\e[0m]     Fusionando estilos CSS de \e[33mtheme.css\e[0m"

# Crear archivo temporal
TEMP=$(mktemp)

# Copiar destino como base
cp "$DESTINO" "$TEMP"

# Variables para tracking
declare -A selectores_existentes

# Extraer selectores del destino (muy simple, busca líneas que terminan en {)
while IFS= read -r linea; do
  if [[ "$linea" =~ ^\s*([^{]+)\s*\{ ]]; then
    selector="${BASH_REMATCH[1]}"
    selectores_existentes["$selector"]=1
  fi
done <"$DESTINO"

# Procesar archivo origen
within_selector=0
current_selector=""
selector_content=""

while IFS= read -r linea; do
  # Detectar inicio de selector
  if [[ "$linea" =~ ^\s*([^{]+)\s*\{ ]]; then
    current_selector="${BASH_REMATCH[1]}"

    # Si selector no existe en destino, agregar todo el bloque
    if [[ -z "${selectores_existentes[$current_selector]}" ]]; then
      echo "" >>"$TEMP"
      echo "$linea" >>"$TEMP"
      within_selector=1
    else
      within_selector=0
    fi
  # Si estamos dentro de un selector nuevo, copiar todo
  elif [[ $within_selector -eq 1 ]]; then
    echo "$linea" >>"$TEMP"
    if [[ "$linea" =~ \} ]]; then
      within_selector=0
    fi
  fi
done <"$ORIGEN"

# Reemplazar destino
mv "$TEMP" "$DESTINO"
