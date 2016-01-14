package com.natanverdes.botigueta;

import android.content.Context;
import android.content.DialogInterface;
import android.database.MatrixCursor;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener{
    private Producto[] todosProductos;
    private ArrayList<ProductoEnCarrito> productosSeleccionados = new ArrayList<>();
    private Spinner selectorProductos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Definimos un listener para el button
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(this);

        // ----------------------- RELLENAR SPINNER ----------------------
        todosProductos = new Producto[29];
        for(int i = 0; i < todosProductos.length; i++){
            todosProductos[i] = new Producto(i);
        }

        MatrixCursor cursor = new MatrixCursor(new String[]{"_id", "nombre", "precio" });

        String[] fila = new String[3];
        for(int i = 0; i < todosProductos.length; i++) {
            fila[0] = Integer.toString(i);
            fila[1] = todosProductos[i].titulo;
            fila[2] = Double.toString(todosProductos[i].precio);
            cursor.addRow(fila);
        }
        SimpleCursorAdapter spinnerAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2 ,
                cursor,
                new String[] { "nombre", "precio" },
                new int[] { android.R.id.text1, android.R.id.text2 },
                0);

        selectorProductos = (Spinner) findViewById(R.id.selector_productos);
        selectorProductos.setAdapter(spinnerAdapter);
    }

    @Override public void onClick(View v) {
        ListView listView = (ListView) findViewById(R.id.listView);


        if(v.getId() == R.id.addButton){
            // ----------------------- RELLENAR LISTVIEW al añadir producto ----------------------
            MatrixCursor selected = (MatrixCursor) selectorProductos.getSelectedItem();
            int idProductoSelected = selected.getPosition();
            Boolean productExists = false;

            // Buscar si ya existen elementos de ese tipo añadidos
            for(ProductoEnCarrito producto : productosSeleccionados){
                if(producto.id == idProductoSelected){
                    producto.cantidad++;
                    productExists = true;
                }
            }

            if(!productExists){
                productosSeleccionados.add(new ProductoEnCarrito(idProductoSelected, 1));
            }

            listView.setAdapter(new ProductoAdapter(this, productosSeleccionados));
        }else if(v.getId() == R.id.deleteButton){
            // TODO peta al eliminar producto de otro index
            int idProductoSelected = (int) v.getTag();


            // Buscar si ya existen elementos de ese tipo añadidos
            for(ProductoEnCarrito producto : productosSeleccionados){
                if(producto.id == idProductoSelected){
                    if(producto.cantidad == 1){
                        productosSeleccionados.remove(producto);
                        break;
                    }else{
                        producto.cantidad--;
                    }
                }
            }
        }

        refreshAccount();
        listView.setAdapter(new ProductoAdapter(this, productosSeleccionados));
    }


    private void refreshAccount(){
        TextView textView = (TextView) findViewById(R.id.chartTotal);
        int totalProductos = 0;
        double totalPrecio = 0.0;
        for(ProductoEnCarrito productoEnCarrito : productosSeleccionados){
            totalProductos += productoEnCarrito.cantidad;
            totalPrecio += productoEnCarrito.precio * productoEnCarrito.cantidad;
        }

        textView.setText("Chart total: " + totalPrecio + "€ (" + totalProductos + " products)");
    }


    private class ViewInfo {
        TextView text1, text2;
        ImageView imageView1;
        Button deleteButton;
        ProductoEnCarrito productoEnCarrito;

        public ViewInfo(View view) {
            text1 = (TextView) view.findViewById(R.id.product_name);
            text2 = (TextView) view.findViewById(R.id.product_price);
            imageView1 = (ImageView) view.findViewById(R.id.product_image);
            deleteButton = (Button) view.findViewById(R.id.deleteButton);
        }

        public void setProducto(ProductoEnCarrito producto) {
            this.productoEnCarrito = producto;
            text1.setText(producto.titulo);

            text2.setText(productoEnCarrito.cantidad + " x " + productoEnCarrito.precio);

            int[] idImagenes = {
                    R.drawable.img000, R.drawable.img001, R.drawable.img002,
                    R.drawable.img003, R.drawable.img004, R.drawable.img005,
                    R.drawable.img006, R.drawable.img007, R.drawable.img008,
                    R.drawable.img009, R.drawable.img010, R.drawable.img011,
                    R.drawable.img012, R.drawable.img013, R.drawable.img014,
                    R.drawable.img015, R.drawable.img016, R.drawable.img017,
                    R.drawable.img018, R.drawable.img019, R.drawable.img020,
                    R.drawable.img021, R.drawable.img022, R.drawable.img023,
                    R.drawable.img024, R.drawable.img025, R.drawable.img026,
                    R.drawable.img027, R.drawable.img028};




            // Imagen
            imageView1.setImageResource(idImagenes[productoEnCarrito.id]);
            deleteButton.setTag(productoEnCarrito.id);
            deleteButton.setOnClickListener(MainActivity.this);
        }
    }



    private class ProductoAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<ProductoEnCarrito> productosEnCarrito;

        public ProductoAdapter(Context context, ArrayList<ProductoEnCarrito> productos) {
            this.context = context;
            this.productosEnCarrito = productos;
        }

        @Override public int getCount() { return productosEnCarrito.size(); }
        @Override public Object getItem(int position) { return productosEnCarrito.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.product_item, parent, false);
                ViewInfo viewInfo = new ViewInfo(view);
                view.setTag(viewInfo);
            }
            ViewInfo viewInfo = (ViewInfo) view.getTag();
            ProductoEnCarrito producto = productosEnCarrito.get(position);
            viewInfo.setProducto(producto);
            return view;
        }
    }



    class Producto{
        int id;
        String titulo;
        String descripcion;
        double precio;
        String imgSrc;

        Producto(int id){
            this.id = id;
            this.titulo = getResources().getStringArray(R.array.productos_titulo)[id];
            this.descripcion = getResources().getStringArray(R.array.productos_descripcion)[id];
            this.precio = Double.parseDouble(getResources().getStringArray(R.array.productos_precio)[id]);
            this.imgSrc = getResources().getStringArray(R.array.productos_img)[id];
        }
    }

    class ProductoEnCarrito extends Producto{
        int cantidad;

        ProductoEnCarrito(int id, int cantidad){
            super(id);
            this.cantidad = cantidad;
        }
    }

    class Carrito{
        ArrayList<ProductoEnCarrito> productosEnCarrito;

        void anadirProducto(ProductoEnCarrito productoEnCarrito){
            this.productosEnCarrito.add(productoEnCarrito);
        }
        void eliminarProducto(ProductoEnCarrito productoEnCarrito){
            this.productosEnCarrito.remove(productoEnCarrito);
        }
    }


}
