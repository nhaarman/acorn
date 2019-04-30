mkdir -p tmp
for f in *.xml
do
  echo "Processing $f.."
  drawio-batch -q 100 $f "tmp/$f.png"
done

echo "Creating gif.."
convert -delay 75 -loop 0 tmp/*.png ${PWD##*/}.gif

rm -r tmp
