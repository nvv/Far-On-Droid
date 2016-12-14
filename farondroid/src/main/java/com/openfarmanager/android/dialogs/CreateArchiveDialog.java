package com.openfarmanager.android.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.archive.ArchiveUtils;
import com.openfarmanager.android.fragments.MainPanel;

/**
 * author: Vlad Namashko
 */
public class CreateArchiveDialog extends BaseFileDialog {

    private EditText mArchiveName;
    private CheckBox mCompression;
    private RadioGroup mArchiveType;
    private RadioGroup mCompressionType;

    private String mDefaultArchiveName;

    public CreateArchiveDialog(Context context, Handler handler, MainPanel inactivePanel, String defaultArchiveName) {
        super(context, handler, inactivePanel);
        mDefaultArchiveName = defaultArchiveName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArchiveName = (EditText) mDialogView.findViewById(R.id.archive_name);
        mCompression = (CheckBox) mDialogView.findViewById(R.id.archive_compression);
        mArchiveType = (RadioGroup) mDialogView.findViewById(R.id.archive_types);
        mCompressionType = (RadioGroup) mDialogView.findViewById(R.id.archive_compression_types);
        mArchiveName.setText(mDefaultArchiveName);

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

        setupVisibility();
    }

    private void setupVisibility() {
        int selectedArchiveType = mArchiveType.getCheckedRadioButtonId();
        mCompressionType.setVisibility(isCompressionEnabled() && selectedArchiveType != R.id.archive_type_zip ?
                View.VISIBLE : View.GONE);
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
            case R.id.archive_compression_xz: return ArchiveUtils.CompressionEnum.xz;
        }
    }

    @Override
    public int getContentView() {
        return R.layout.dialog_create_archive;
    }

    @Override
    protected boolean validate() {
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

    @Override
    protected void execute() {
        mHandler.sendMessage(mHandler.obtainMessage(MainPanel.FILE_CREATE_ARCHIVE,
                new CreateArchiveResult(mInactivePanel, getArchiveName(), getArchiveType(), isCompressionEnabled(),
                        isCompressionEnabled() && getArchiveType() != ArchiveUtils.ArchiveType.zip ? getCompression() : null)));
    }

    public class CreateArchiveResult {
        public MainPanel inactivePanel;
        public String archiveName;
        public ArchiveUtils.ArchiveType archiveType;
        public boolean isCompressionEnabled;
        public ArchiveUtils.CompressionEnum compression;

        public CreateArchiveResult(MainPanel panel, String name, ArchiveUtils.ArchiveType type,
                                   boolean isCompressionEnabled, ArchiveUtils.CompressionEnum compression) {
            inactivePanel = panel;
            archiveName = name;
            archiveType = type;
            this.isCompressionEnabled = isCompressionEnabled;
            this.compression = compression;
        }
    }
}
