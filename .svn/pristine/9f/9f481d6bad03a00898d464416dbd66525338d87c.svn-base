package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.core.archive.ArchiveUtils;
import com.openfarmanager.android.utils.ParcelableWrapper;

/**
 * @author Vlad Namashko
 */
public class CreateArchiveDialog extends BaseDialog {

    private EditText mArchiveName;
    private CheckBox mCompression;
    private RadioGroup mArchiveType;
    private RadioGroup mCompressionType;

    public static CreateArchiveDialog newInstance(AbstractCommand command, String defaultArchiveName) {
        CreateArchiveDialog dialog = new CreateArchiveDialog();
        Bundle args = new Bundle();

        args.putParcelable("command", new ParcelableWrapper<AbstractCommand>(command));
        args.putString("defaultArchiveName", defaultArchiveName);

        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Action_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getSafeString(R.string.app_name));

        View view = inflater.inflate(R.layout.dialog_create_archive, container, false);

        mArchiveName = (EditText) view.findViewById(R.id.archive_name);
        mCompression = (CheckBox) view.findViewById(R.id.archive_compression);
        mArchiveType = (RadioGroup) view.findViewById(R.id.archive_types);
        mCompressionType = (RadioGroup) view.findViewById(R.id.archive_compression_types);

        mArchiveName.setText(getArguments().getString("defaultArchiveName"));

        mCompression.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setupVisibility();
            }
        });

        mArchiveType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                setupVisibility();
            }
        });

        mArchiveName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mArchiveName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate() && CreateArchiveDialog.this.getArguments().getParcelable("command") != null) {
                    okClicked();
                }
            }
        });

        mArchiveName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mArchiveName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        setupVisibility();
        return view;
    }

    private void setupVisibility() {
        int selectedArchiveType = mArchiveType.getCheckedRadioButtonId();
        mCompressionType.setVisibility(isCompressionEnabled() && selectedArchiveType != R.id.archive_type_zip ?
                View.VISIBLE : View.GONE);
    }

    private boolean validate() {
        String archiveName = getArchiveName();
        if (TextUtils.isEmpty(archiveName)) {
            mArchiveName.setError(getSafeString(R.string.error_enter_archive_name));
            return false;
        }

        int selectedArchiveType = mArchiveType.getCheckedRadioButtonId();
        if (selectedArchiveType == R.id.archive_type_ar) {
            // file name max symbols
            // if compression disabled - 16 - 3 (a.ar)
            // if compression enabled - 16 - (4 + compression extension)
            int maxLength = 16 - (isCompressionEnabled() ? ArchiveUtils.CompressionEnum.toString(getCompression()).length() + 4 : 3);
            if (mArchiveName.length() > maxLength) {
                mArchiveName.setError(getSafeString(R.string.error_ar_archive_too_long_name));
                return false;
            }
        }

        return true;
    }

    private boolean isCompressionEnabled() {
        return mCompression.isChecked();
    }

    private String getArchiveName() {
        return mArchiveName.getText().toString().trim();
    }

    private ArchiveUtils.ArchiveType getArchiveType() {
        int archiveTypeId = mArchiveType.getCheckedRadioButtonId();

        switch (archiveTypeId) {
            case R.id.archive_type_zip: default: return ArchiveUtils.ArchiveType.zip;
            case R.id.archive_type_tar: return ArchiveUtils.ArchiveType.tar;
            case R.id.archive_type_ar: return ArchiveUtils.ArchiveType.ar;
            case R.id.archive_type_jar: return ArchiveUtils.ArchiveType.jar;
            case R.id.archive_type_cpio: return ArchiveUtils.ArchiveType.cpio;
        }
    }

    private ArchiveUtils.CompressionEnum getCompression() {
        int compressionId = mCompressionType.getCheckedRadioButtonId();

        switch (compressionId) {
            case R.id.archive_compression_gzip: default: return ArchiveUtils.CompressionEnum.gzip;
            case R.id.archive_compression_bzip2: return ArchiveUtils.CompressionEnum.bzip2;
        }
    }

    @SuppressWarnings("unchecked")
    private void okClicked() {
        dismiss();
        AbstractCommand command = ((ParcelableWrapper<AbstractCommand>) getArguments().getParcelable("command")).value;

        command.execute(App.sInstance.getFileSystemController().getInactivePanel(), getArchiveName(), getArchiveType(), isCompressionEnabled(),
                isCompressionEnabled() && getArchiveType() != ArchiveUtils.ArchiveType.zip ? getCompression() : null);
    }

}
