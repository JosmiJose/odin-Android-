package btm.odinandroidwallet.ui.request;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.ViewPagerAdapter;
import btm.odinandroidwallet.networking.retrofit.pojo.UserProfileResponse;
import btm.odinandroidwallet.ui.auth.PinEntryActivity;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.history.HistoryFragment;
import btm.odinandroidwallet.util.PrefsUtil;

/**
 * Created by Waleed on 6/3/2018.
 */

public class RequestFragment extends Fragment {
    ImageView image_qr;
    ImageView image_qr_error;
    TextView textView_receiving_address;
    ProgressBar progressBar;
    private String token="odin";
    private PrefsUtil prefsUtil;
    public static RequestFragment newInstance(String token) {
        RequestFragment fragment = new RequestFragment();
        fragment.token=token;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receive, container, false);
        prefsUtil=new PrefsUtil(getActivity());
        Gson gson = new Gson();
        String json = prefsUtil.getValue(PrefsUtil.USER_PROFILE, "");
        UserProfileResponse obj = gson.fromJson(json, UserProfileResponse.class);
        image_qr = view.findViewById(R.id.image_qr);
        image_qr_error=view.findViewById(R.id.image_qr_error);
        textView_receiving_address = view.findViewById(R.id.textview_receiving_address);
        if(obj != null && obj.result.walletId != null) {
            textView_receiving_address.setText(obj.result.walletId.toString());
            textView_receiving_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showClipboardWarning();
                }
            });
        }
        else
        {
            textView_receiving_address.setText(getResources().getString(R.string.account_must_be_verified_qr));
            view.findViewById(R.id.textview_receive_helper).setVisibility(View.GONE);
        }


        progressBar = view.findViewById(R.id.progressbar);
        if(obj != null && obj.result.walletId != null) {
            if (token.equals("odin")) {
                try {
                    JSONObject object = new JSONObject();
                    object.put("to", obj.result.walletId);
                    object.put("mode", "erc20__transfer");
                    object.put("contract_address", "0x7cB57B5A97eAbe94205C07890BE4c1aD31E486A8");
                    generateQrCode(object.toString());
                } catch (Exception ex) {
                }
            } else if (token.equals("ether")) {
                    generateQrCode(obj.result.walletId.toString());
            }
        }
        else
        {
            generateQrCode("Please update your profile");
            image_qr.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            image_qr_error.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void showClipboardWarning() {
        String address = textView_receiving_address.getText().toString();

        new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                .setTitle(R.string.app_name)
                .setMessage(R.string.receive_address_to_clipboard)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> copyToClipboard(getActivity(), address))
                .setNegativeButton(R.string.no, null)
                .setCancelable(false)
                .create()
                .show();

    }

    public boolean copyToClipboard(Context context, String text) {
        try {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText(
                                "Send Address", text);
                clipboard.setPrimaryClip(clip);
                showToast(R.string.copied_to_clipboard, ToastCustom.TYPE_GENERAL);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(getActivity(), getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }

    private void generateQrCode(String uri) {
        showQrLoading();
        // compositeDisposable.clear()
        String text = uri; // Whatever you need to encode in the QR code
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            showQrCode(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    private void showQrLoading() {
        image_qr.setVisibility(View.INVISIBLE);
        textView_receiving_address.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showQrCode(Bitmap bitmap) {
        progressBar.setVisibility(View.INVISIBLE);
        image_qr.setVisibility(View.VISIBLE);
        textView_receiving_address.setVisibility(View.VISIBLE);
        image_qr.setImageBitmap(bitmap);

    }

}
