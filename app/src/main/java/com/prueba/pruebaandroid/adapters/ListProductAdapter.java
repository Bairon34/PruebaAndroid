package com.prueba.pruebaandroid.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.prueba.pruebaandroid.Model.ProductModel;
import com.prueba.pruebaandroid.R;

import java.util.List;

public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ListProductViewHolder> {



    private Context context1;
    private List<ProductModel> producto;
    private String model;
    private String price_p, img_p, name_article,cant_producto,observacion,categotia;
    private FirebaseDatabase database_user = FirebaseDatabase.getInstance();


    Dialog myDialog;
    int position_edit=0;

    private String check_descripcion;
    private String check_name;
    private String check_valor;
    private String check_foto;

    public ListProductAdapter(Context context1, List<ProductModel> producto) {
        this.context1 = context1;
        this.producto = producto;
    }


    @NonNull
    @Override
    public ListProductViewHolder onCreateViewHolder( ViewGroup parent1, int viewType1) {
        View v1 = LayoutInflater.from(parent1.getContext()).inflate(R.layout.item_product_fact, parent1, false);
        return  new ListProductAdapter.ListProductViewHolder(v1);
    }

    @Override
    public void onBindViewHolder(@NonNull ListProductAdapter.ListProductViewHolder holder_pro1, int position1) {

        img_p = String.valueOf(producto.get(position1).getPhoto());
        holder_pro1.rcv_text_pro.setText(String.valueOf(producto.get(position1).getName()));
        holder_pro1.rcv_price_pro.setText("$ "+String.valueOf(producto.get(position1).getPrice()));



        Glide.with(context1)
                .load(img_p)
                .centerCrop()
                .into(holder_pro1.rcv_img_pro);


        holder_pro1.setOnClickListeners();

    }

    @Override
    public int getItemCount() {
       return  producto.size();
    }

    public class ListProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView rcv_price_pro;
        TextView rcv_text_pro;
        ImageView rcv_img_pro;
        Button  btnProduct;


        public ListProductViewHolder(@NonNull View itemView1) {
            super(itemView1);


            context1 = itemView1.getContext();
            rcv_img_pro =  itemView1.findViewById(R.id.imgproduct);
            rcv_text_pro = itemView1.findViewById(R.id.textproduct);
            rcv_price_pro = (TextView) itemView1.findViewById(R.id.textprecioantesproduct);

            btnProduct = (Button) itemView1.findViewById(R.id.btnProduct);


        }


        void setOnClickListeners() {

            //btnMas.setOnClickListener(this);
            btnProduct.setOnClickListener(this);



        }


        @Override
        public void onClick(View view) {


            switch (view.getId()) {

                case R.id.btnProduct:

                    myDialog = new Dialog(context1);
                    myDialog.setContentView(R.layout.modal);


                    ImageView imgfotoProduct;
                    TextView txtProduct;
                    TextView txtDescription;
                    TextView txtPrice;
                    Button btnBuy;


                    position_edit = getAdapterPosition();
                    check_descripcion = producto.get(position_edit).getDescription();
                    check_name = producto.get(position_edit).getName();
                    check_valor = producto.get(position_edit).getPrice();
                    check_foto = producto.get(position_edit).getPhoto();


                    btnBuy = myDialog.findViewById(R.id.btnBuy);


                    txtProduct  = myDialog.findViewById(R.id.txtProduct);
                    txtDescription  = myDialog.findViewById(R.id.txtDescription);
                    txtPrice  = myDialog.findViewById(R.id.txtPrice);
                    imgfotoProduct  = myDialog.findViewById(R.id.imgfotoProduct);




                    Glide.with(context1)
                            .load(check_foto)
                            .centerCrop()
                            .into(imgfotoProduct);

                    txtProduct.setText(check_name);
                    txtDescription.setText(check_descripcion);

                    txtPrice.setText("$"+check_valor);



                    btnBuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(context1, "producto comprado proximamnete pasarela de pagos", Toast.LENGTH_SHORT).show();
                        }
                    });



                    myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    myDialog.show();


                    break;
            }


        }
    }
}
