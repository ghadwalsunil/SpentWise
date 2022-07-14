package com.alpha.spentwise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alpha.spentwise.R;
import com.alpha.spentwise.customDataObjects.ContactDetails;
import com.alpha.spentwise.customDataObjects.CurrencyDetails;
import com.alpha.spentwise.dataManager.UserProjectDataHolder;

import java.util.ArrayList;
import java.util.List;

import static com.alpha.spentwise.utils.CustomFunctions.optionMenu;

public class SelectContactActivity extends AppCompatActivity {

    private ListView contactListView;
    private List<ContactDetails> contactDetailsList;
    private ArrayAdapter<ContactDetails> contactDetailsArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        contactListView = findViewById(R.id.selectContact_listView);

        Toolbar toolbar=findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(R.string.selectContactActivityTitle);

        contactDetailsList = getContactList();
        contactDetailsArrayAdapter = new ArrayAdapter<>(SelectContactActivity.this,android.R.layout.simple_list_item_1,contactDetailsList);
        contactListView.setAdapter(contactDetailsArrayAdapter);

        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((UserProjectDataHolder)getApplication()).setSelectedContactDetails(contactDetailsArrayAdapter.getItem(position));
                Intent intent = new Intent(SelectContactActivity.this,ManageEntryActivity.class);
                startActivity(intent);
            }
        });
    }

    private List<ContactDetails> getContactList(){

        List<ContactDetails> contactDetailsList = new ArrayList<>();

        Cursor cursorPhones = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                },
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        );

        while (cursorPhones.moveToNext()) {

            String displayName = cursorPhones.getString(cursorPhones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = cursorPhones.getString(cursorPhones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            ContactDetails contactDetails = new ContactDetails(displayName,number);
            contactDetailsList.add(contactDetails);
        }

        cursorPhones.close();

        return contactDetailsList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        optionMenu(id,this,((UserProjectDataHolder)getApplication()).getLoggedInUser());

        return true;
    }
}