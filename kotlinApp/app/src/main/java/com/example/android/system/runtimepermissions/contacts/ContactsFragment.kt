/*
* Copyright 2015 The Android Open Source Project
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

package com.example.android.system.runtimepermissions.contacts

import android.content.ContentProviderOperation
import android.content.OperationApplicationException
import android.database.Cursor
import android.os.Bundle
import android.os.RemoteException
import android.provider.ContactsContract
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.system.runtimepermissions.R
import kotlinx.android.synthetic.main.fragment_contacts.*
import java.util.ArrayList

/**
 * Displays the first contact stored on the device and contains an option to add a dummy contact.
 *
 *
 * This Fragment is only used to illustrate that access to the Contacts ContentProvider API has
 * been granted (or denied) as part of the runtime permissions model. It is not relevant for the
 * use
 * of the permissions API.
 *
 *
 * This fragments demonstrates a basic use case for accessing the Contacts Provider. The
 * implementation is based on the training guide available here:
 * https://developer.android.com/training/contacts-provider/retrieve-names.html
 */
class ContactsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_contacts, container, false).apply {
                // Register a listener to add a dummy contact when a button is clicked.
                contactAddButton.setOnClickListener { insertDummyContact() }

                // Register a listener to display the first contact when a button is clicked.
                contactLoadButton.setOnClickListener { loadContact() }
            }

    /**
     * Restart the Loader to query the Contacts content provider to display the first contact.
     */
    private fun loadContact() {
        loaderManager.restartLoader(0, Bundle(), this)
    }

    /**
     * Initialises a new [CursorLoader] that queries the [ContactsContract].
     */
    override fun onCreateLoader(i: Int, bundle: Bundle): Loader<Cursor> =
            CursorLoader(activity, ContactsContract.Contacts.CONTENT_URI, PROJECTION, null, null,
                    ORDER)


    /**
     * Dislays either the name of the first contact or a message.
     */
    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        cursor?.run {
            if (count > 0) {
                moveToFirst()
                val name = getString(getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                contactMessage.text = resources.getString(R.string.contacts_string, count, name)
            } else {
                contactMessage.text = resources.getString(R.string.contacts_empty)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        contactMessage.text = getString(R.string.contacts_empty)
    }

    /**
     * Accesses the Contacts content provider directly to insert a new contact.
     *
     *
     * The contact is called "__DUMMY ENTRY" and only contains a name.
     */
    private fun insertDummyContact() {
        // Two operations are needed to insert a new contact.
        val operations = ArrayList<ContentProviderOperation>(2)

        // First, set up a new raw contact.
        operations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build())

        // Next, set the name for the contact.
        operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        DUMMY_CONTACT_NAME)
                .build())

        // Apply the operations.
        try {
            activity.contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
        } catch (e: RemoteException) {
            Snackbar.make(contactMessage.rootView, "Could not add a new contact: " + e.message,
                    Snackbar.LENGTH_LONG)
        } catch (e: OperationApplicationException) {
            Snackbar.make(contactMessage.rootView, "Could not add a new contact: " + e.message,
                    Snackbar.LENGTH_LONG)
        }

    }

    companion object {

        /**
         * Projection for the content provider query includes the id and primary name of a contact.
         */
        @JvmField val PROJECTION = arrayOf(ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
        /**
         * Sort order for the query. Sorted by primary name in ascending order.
         */
        const val ORDER = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC"
        const val DUMMY_CONTACT_NAME = "__DUMMY CONTACT from runtime permissions sample"

        /**
         * Creates a new instance of a ContactsFragment.
         */
        fun newInstance() = ContactsFragment()
    }
}
