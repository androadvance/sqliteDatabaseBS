package com.example.sqlitedatabase.MyCart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import com.example.sqlitedatabase.R;
import java.util.List;

public class U_CartItemAdapter extends BaseAdapter {

    private Context context;
    private List<U_CartItemList> uCartItemLists;

    public U_CartItemAdapter(Context context, List<U_CartItemList> uCartItemLists) {
        this.context = context;
        this.uCartItemLists = uCartItemLists;
    }

    @Override
    public int getCount() {
        return uCartItemLists.size();
    }

    @Override
    public Object getItem(int position) {
        return uCartItemLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        @SuppressLint("ViewHolder")
        View v = View.inflate(context, R.layout.confirm_order_listitem, null);

        TextView tv_size = v.findViewById(R.id.tv_size);
        EditText tv_qty = v.findViewById(R.id.tv_qty);
        TextView tv_rate = v.findViewById(R.id.tv_rate);
        TextView tv_amount = v.findViewById(R.id.tv_amount);

        final U_CartItemList ListItem = uCartItemLists.get(position);

        tv_size.setText(uCartItemLists.get(position).getSize());
        tv_qty.setText(uCartItemLists.get(position).getQty());
        tv_rate.setText(uCartItemLists.get(position).getRate());
        tv_amount.setText(uCartItemLists.get(position).getAmount());

        tv_qty.setTag(position);

        tv_qty.addTextChangedListener(new CustomTextWatcher(tv_qty));

        return v;
    }

    private class CustomTextWatcher implements TextWatcher {

        private EditText mEditText;

        public CustomTextWatcher(EditText e) {
            mEditText = e;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable editable) {

            int row = 0;

            try {
                String s = mEditText.getTag().toString();
                row = Integer.parseInt(s);
                CartItemUpdate.mOrderlist.get(row).setQty(editable.toString());
            } catch (Exception er) {
                er.printStackTrace();
            }
        }
    }
}
