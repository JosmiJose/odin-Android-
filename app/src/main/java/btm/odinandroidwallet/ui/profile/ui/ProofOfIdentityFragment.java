/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package btm.odinandroidwallet.ui.profile.ui;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.ui.customviews.ToastCustom;
import btm.odinandroidwallet.ui.profile.model.ProofOfIdentityPage;
import btm.odinandroidwallet.ui.profile.model.ResdentialPage;
import btm.odinandroidwallet.ui.views.ProgressImageView;
import btm.odinandroidwallet.util.AppUtil;
import btm.odinandroidwallet.util.FileUtils;
import btm.odinandroidwallet.util.MediaUtils;
import btm.odinandroidwallet.util.RealPathUtil;

public class ProofOfIdentityFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ProofOfIdentityPage mPage;
    EditText etNumber;
    EditText expiryDate;
    ImageView fImage,bImage,cImage;
    private Uri photoUploadUri;
    private static final int MEDIA_TAKE_PHOTO_RESULT = 2;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PERMISSIONS_CAMERA = 10;
    private static final int MEDIA_PICK_RESULT = 1;
    private static final int THUMBNAIL_SIZE = 128;
    private static final int FRONT_IMAGE_ID= 1;
    private static final int BACK_IMAGE_ID = 2;
    private static final int SELFIE_IMAGE_3= 3;
    ProgressImageView frontImageView,backImageView,selfieImageView;
    private static final String TAG = "UPDATEPROFILE"; // logging tag
    private static int imageType=0;
    String fImgPath;
    String bImgPath;
    String sImgPath;
    private int pickMode=0;
    CountryCodePicker countryCodePicker;
    DatePickerDialog dpd;
    int currentSelectedYear=0;
    int currentSelectedMonth=0;
    int currentSelectedDay=0;
    InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source.length() > 0) {
                if (!Character.isLetterOrDigit(source.charAt(0))) {
                    showToast(R.string.does_not_accept_special_characters, ToastCustom.TYPE_ERROR);
                    return "";
                }
                else if(!AppUtil.isEnglishAlphabets(source.charAt(0))){
                    showToast(R.string.allow_only_english_alphabet, ToastCustom.TYPE_ERROR);
                    return "";
                }
            }
            return null;
        }
    };
    public static ProofOfIdentityFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ProofOfIdentityFragment fragment = new ProofOfIdentityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ProofOfIdentityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (ProofOfIdentityPage) mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_proof_of_identity, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());
        countryCodePicker = (CountryCodePicker) rootView.findViewById(R.id.select_country);
        if (!TextUtils.isEmpty(mPage.getData().getString(ProofOfIdentityPage.NATIONALITY_KEY))) {
            countryCodePicker.setCountryForNameCode(mPage.getData().getString(ProofOfIdentityPage.NATIONALITY_KEY));
        } else {
            setData(ProofOfIdentityPage.NATIONALITY_KEY, countryCodePicker.getDefaultCountryNameCode());
        }
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                setData(ProofOfIdentityPage.NATIONALITY_KEY, countryCodePicker.getSelectedCountryNameCode());
            }
        });
        Spinner spinner =(Spinner)rootView.findViewById(R.id.document_type);
        String[] types = getResources().getStringArray(R.array.document_type);
        ArrayAdapter<String> spinnerCountShoesArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,types);
        spinner.setAdapter(spinnerCountShoesArrayAdapter);
        int index=0;
        for(int i=0;i<types.length;i++)
        {

            if(types[i].equals(mPage.getData().getString(ProofOfIdentityPage.DOC_TYPE_DATA_KEY)))
            {
            index=i;
            }
        }
        spinner.setSelection(index);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String[] types = getResources().getStringArray(R.array.document_type);
                    String type=types[i];
                    setData(ProofOfIdentityPage.DOC_TYPE_DATA_KEY,type);
                    //hide expiry Date only for ID but show for passport and driving license
                    if(type.equals(types[0])){
                        expiryDate.setVisibility(View.GONE);
                    }else{
                        expiryDate.setVisibility(View.VISIBLE);
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        etNumber=rootView.findViewById(R.id.number);
        etNumber.setText(mPage.getData().getString(ProofOfIdentityPage.NUMBER_KEY));
        etNumber.setFilters(new InputFilter[] { filter ,new InputFilter.LengthFilter(AppUtil.inputLength) });
        expiryDate = rootView.findViewById(R.id.expiry_date);
        expiryDate.setText(mPage.getData().getString(ProofOfIdentityPage.EXPIRY_DATE_KEY));
        fImage=rootView.findViewById(R.id.f_image);
        fImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageType=1;
            showGuidlinesDialog();
            }
        });
        bImage=rootView.findViewById(R.id.b_image);
        bImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageType=2;
                showGuidlinesDialog();
            }
        });
        cImage=rootView.findViewById(R.id.s_image);
        cImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageType=3;
                showGuidlinesDialog();
            }
        });
        frontImageView=rootView.findViewById(R.id.f_image_view);
        backImageView=rootView.findViewById(R.id.b_image_view);
        selfieImageView=rootView.findViewById(R.id.s_image_view);
        //show fImage if it exist
        if(!TextUtils.isEmpty(mPage.getData().getString(ProofOfIdentityPage.FRONT_IMAGE_KEY))){
            ShowFrontImage();
            prepareImage(frontImageView,Uri.parse(mPage.getData().getString(ProofOfIdentityPage.FRONT_IMAGE_KEY)),FRONT_IMAGE_ID);
        }
        //show bImage if it exist
        if(!TextUtils.isEmpty(mPage.getData().getString(ProofOfIdentityPage.BACK_IMAGE_KEY))){
            ShowBackImage();
            prepareImage(backImageView,Uri.parse(mPage.getData().getString(ProofOfIdentityPage.BACK_IMAGE_KEY)),BACK_IMAGE_ID);
        }
        //show sImage if it exist
        if(!TextUtils.isEmpty(mPage.getData().getString(ProofOfIdentityPage.SELFIE_IMAGE_KEY))){
            ShowSelfieImage();
            prepareImage(selfieImageView,Uri.parse(mPage.getData().getString(ProofOfIdentityPage.SELFIE_IMAGE_KEY)),SELFIE_IMAGE_3);
        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        expiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate();
            }
        });
        expiryDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                   showDate();
                }
            }
        });
        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setData(ProofOfIdentityPage.NUMBER_KEY, (editable != null) ? editable.toString() : null);
            }
        });

    }

    private void setData(String key, String value) {
        mPage.getData().putString(key, value);
        mPage.notifyDataChanged();
    }

    private void showDate() {
        Calendar now = Calendar.getInstance();
        if(dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    ProofOfIdentityFragment.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        }else
        {
            if(currentSelectedYear != 0) {
                dpd.initialize(
                        ProofOfIdentityFragment.this,
                        currentSelectedYear,
                        currentSelectedMonth,
                        currentSelectedDay
                );
            }
            else
            {
                dpd.initialize(
                        ProofOfIdentityFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
            }
        }
        Dialog dialogFrg=dpd.getDialog();
        if(dialogFrg!=null && dialogFrg.isShowing()) {
            // no need to call dialog.show(ft, "DatePicker");
        } else {
            dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
        }

    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        currentSelectedYear=year;
        currentSelectedMonth=monthOfYear;
        currentSelectedDay=dayOfMonth;
        //Formate date to DD/MM/YYYY
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat disaplayFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd");
        String displayDate = disaplayFormat.format(calendar.getTime());
        String date = apiFormat.format(calendar.getTime());
        expiryDate.setText(displayDate);
        setData(ProofOfIdentityPage.EXPIRY_DATE_KEY, date);
    }
    private void showGuidlinesDialog()
    {
        new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                .setTitle(R.string.upload_photo_guidlines_title)
                .setMessage(R.string.upload_photo_guidlines)
                .setPositiveButton(R.string.dialog_continue, (dialog1, which) -> {
                    openPickDialog();
                })
                .setNegativeButton(R.string.cancel,null)
                .show();
    }
    private void openPickDialog() {
        final int CHOICE_TAKE = 0;
        final int CHOICE_PICK = 1;
        CharSequence[] choices = new CharSequence[2];
        choices[CHOICE_TAKE] = getString(R.string.action_photo_take);
        choices[CHOICE_PICK] = getString(R.string.action_photo_pick);
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            switch (which) {
                case CHOICE_TAKE: {
                    pickMode=1;
                 onCameraPick();
                    break;
                }
                case CHOICE_PICK: {
                    pickMode=2;
                    onMediaPick();
                    break;
                }
            }
        };
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setItems(choices, listener)
                .create();
        dialog.show();
    }
    private void initiateCameraApp() {
        // We don't need to ask for permission in this case, because the used calls require
        // android.permission.WRITE_EXTERNAL_STORAGE only on SDKs *older* than Kitkat, which was
        // way before permission dialogues have been introduced.
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createNewImageFile();
                switch (imageType)
                {
                    case 1:
                        fImgPath=photoFile.getAbsolutePath();
                        break;
                    case 2:
                        bImgPath=photoFile.getAbsolutePath();
                        break;
                    case 3:
                        sImgPath=photoFile.getAbsolutePath();
                        break;
                }

            } catch (IOException ex) {
                showErrorToast(R.string.error_media_upload_opening);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUploadUri = FileProvider.getUriForFile(getActivity(),
                        "btm.odinandroidwallet.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUploadUri);
                startActivityForResult(intent, MEDIA_TAKE_PHOTO_RESULT);
            }
        }
    }
    private void showErrorToast(@StringRes int message) {
        showToast(message, ToastCustom.TYPE_ERROR);
    }
    public void showToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(getActivity(), getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }
    @NonNull
    private File createNewImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "ODIN_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

    }
    private void onCameraPick() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_CAMERA);
        } else {
            initiateCameraApp();
        }
    }
    private void onMediaPick() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
           requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            initiateMediaPicking();
        }
    }
    private void initiateMediaPicking() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent.setType("image/*");
        } else {
            String[] mimeTypes = new String[]{"image/*"};
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(intent, MEDIA_PICK_RESULT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initiateMediaPicking();
                } /*else {
                    doErrorDialog(R.string.error_media_upload_permission, R.string.action_retry,
                            v -> onMediaPick());
                }*/
                break;

            }
            case PERMISSIONS_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initiateCameraApp();
                }
            }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == MEDIA_PICK_RESULT && data != null) {
            Uri uri = data.getData();
            long mediaSize = MediaUtils.getMediaSize(getActivity().getContentResolver(), uri);
            pickMedia(uri, mediaSize);
        } else if (resultCode == getActivity().RESULT_OK && requestCode == MEDIA_TAKE_PHOTO_RESULT) {
            long mediaSize = MediaUtils.getMediaSize(getActivity().getContentResolver(), photoUploadUri);
            pickMedia(photoUploadUri, mediaSize);
        }
    }
    private void pickMedia(Uri uri, long mediaSize) {
        if (mediaSize == MediaUtils.MEDIA_SIZE_UNKNOWN) {
            showErrorToast(R.string.error_media_upload_opening);
            return;
        } else if (mediaSize > MediaUtils.MEDIA_MAX_LIMIT) {
            showErrorToast(R.string.error_media_upload_size);
            return;
        }

        ContentResolver contentResolver = getActivity().getContentResolver();
        String mimeType = contentResolver.getType(uri);
        if (mimeType != null) {
            String topLevelType = mimeType.substring(0, mimeType.indexOf('/'));
            switch (topLevelType) {
                case "image": {
                    Bitmap bitmap = MediaUtils.getImageThumbnail(contentResolver, uri, THUMBNAIL_SIZE);
                    if (bitmap != null) {
                        switch (imageType)
                        {
                            case 1:
                                if(pickMode == 2) {
                                    fImgPath = getRealPath(uri);
                                }
                                parseFrontImage(bitmap, uri, mediaSize, null);

                                break;
                            case 2:
                                if(pickMode == 2) {
                                    bImgPath = getRealPath(uri);
                                }
                                parseBackImage(bitmap, uri, mediaSize, null);

                                break;
                            case 3:
                                if(pickMode == 2) {
                                    sImgPath = getRealPath(uri);
                                }
                                parseSelfieImage(bitmap, uri, mediaSize, null);

                                break;
                        }

                    } else {
                        showErrorToast(R.string.error_media_upload_opening);
                    }
                    break;
                }
                default: {
                    showErrorToast(R.string.error_media_upload_type);
                    break;
                }
            }
        } else {
            showErrorToast(R.string.error_media_upload_type);
        }
    }
    private String getRealPath(Uri uri)
    {
        String realPath="";

        try
           {

                realPath= FileUtils.getFileFromUri(getActivity(),uri).getAbsolutePath();
               // realPath= FileUtils.getFileFromUri(getActivity(),uri).getPath();
            }catch (Exception ex){
            ex.printStackTrace();
        }

        return realPath;
    }
    private void parseFrontImage(Bitmap preview, Uri uri, long mediaSize,
                                 @Nullable String description) {
        ShowFrontImage();
        prepareImage(frontImageView,preview,FRONT_IMAGE_ID);
        setData(ProofOfIdentityPage.FRONT_IMAGE_KEY, fImgPath);

    }
    private void parseBackImage(Bitmap preview, Uri uri, long mediaSize,
                                 @Nullable String description) {
        ShowBackImage();
        prepareImage(backImageView,preview,BACK_IMAGE_ID);
        setData(ProofOfIdentityPage.BACK_IMAGE_KEY,bImgPath);

    }
    private void parseSelfieImage(Bitmap preview, Uri uri, long mediaSize,
                                 @Nullable String description) {
        ShowSelfieImage();
        prepareImage(selfieImageView,preview,SELFIE_IMAGE_3);
        setData(ProofOfIdentityPage.SELFIE_IMAGE_KEY, sImgPath);

    }
    private void prepareImage(ProgressImageView image,Bitmap preview,int removeId) {
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setImageBitmap(preview);
        image.setOnClickListener(v -> onMediaClick(v,removeId));
        image.setContentDescription(getString(R.string.action_delete));
    }
    private void prepareImage(ProgressImageView image, Uri uri,int removeId) {
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        File file = new File(uri.getPath());
        Picasso.with(getContext())
                .load(file)
                .resize(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
                .into(image);
        image.setOnClickListener(v -> onMediaClick(v,removeId));
        image.setContentDescription(getString(R.string.action_delete));
    }

    private void onMediaClick(View view, int removeId) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
//        final int removeId = 2;
        popup.getMenu().add(0, removeId, 0, R.string.action_remove_media);
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case 1:
                    RemoveFrontImage();
                    break;
                case 2:
                    RemoveBackImage();
                    break;
                case 3:
                    RemoveSelfieImage();
                    break;
            }
            return true;
        });
        popup.show();
    }

    private void RemoveFrontImage() {
        frontImageView.setVisibility(View.GONE);
        setData(ProofOfIdentityPage.FRONT_IMAGE_KEY,"");
    }
    private void ShowFrontImage() {
        frontImageView.setVisibility(View.VISIBLE);
    }
    private void RemoveBackImage() {
        backImageView.setVisibility(View.GONE);
        setData(ProofOfIdentityPage.BACK_IMAGE_KEY,"");
    }
    private void ShowBackImage() {
        backImageView.setVisibility(View.VISIBLE);
    }
    private void RemoveSelfieImage() {
        selfieImageView.setVisibility(View.GONE);
        setData(ProofOfIdentityPage.SELFIE_IMAGE_KEY,"");
    }
    private void ShowSelfieImage() {
        selfieImageView.setVisibility(View.VISIBLE);
    }

    private String formateDate(Calendar calendar){
        return "";
    }
}
