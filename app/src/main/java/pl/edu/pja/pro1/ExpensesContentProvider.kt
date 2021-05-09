package pl.edu.pja.pro1

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class ExpensesContentProvider : ContentProvider() {
    private val uriMatcher =
        UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, EXPENSES_PATH, EXPENSES)
            addURI(AUTHORITY, EXPENSES_ID_PATH, EXPENSES_ID)
        }

    private val database by lazy {
        val appContext = context?.applicationContext ?: throw IllegalStateException()
        val entryPoint =
            EntryPointAccessors.fromApplication(appContext, ContentProviderEntryPoint::class.java)
        return@lazy entryPoint.provideAppDatabase()
    }

    override fun onCreate(): Boolean {
        return true;
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        when (uriMatcher.match(uri)) {
            EXPENSES -> {
                val result = database.openHelper.writableDatabase.update(
                    AppDatabase.EXPENSE_TABLE,
                    SQLiteDatabase.CONFLICT_ABORT,
                    values,
                    selection,
                    selectionArgs
                )

                database.openHelper.writableDatabase.close()
                return result
            }

            EXPENSES_ID -> {
                val mutableSelectionArgs =
                    selectionArgs?.toMutableList() ?: throw IllegalStateException()
                mutableSelectionArgs.add(uri.lastPathSegment.toString())

                val result = database.openHelper.writableDatabase.update(
                    AppDatabase.EXPENSE_TABLE,
                    SQLiteDatabase.CONFLICT_ABORT,
                    values,
                    "$selection and id = ?s",
                    mutableSelectionArgs.toTypedArray()
                )

                database.openHelper.writableDatabase.close()
                return result
            }

            else -> throw Exception()
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        when (uriMatcher.match(uri)) {
            EXPENSES -> {
                val result = database.openHelper.writableDatabase.delete(
                    AppDatabase.EXPENSE_TABLE,
                    selection,
                    selectionArgs
                )

                database.openHelper.writableDatabase.close()
                return result
            }

            EXPENSES_ID -> {
                val mutableSelectionArgs =
                    selectionArgs?.toMutableList() ?: throw IllegalStateException()
                mutableSelectionArgs.add(uri.lastPathSegment.toString())

                val result = database.openHelper.writableDatabase.delete(
                    AppDatabase.EXPENSE_TABLE,
                    "$selection and id = ?s",
                    mutableSelectionArgs.toTypedArray()
                )

                database.openHelper.writableDatabase.close()
                return result
            }

            else -> throw Exception()
        }

    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            EXPENSES_ID -> MIME_CURSOR_LIST
            EXPENSES -> MIME_CURSOR
            else -> throw Exception()
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (uriMatcher.match(uri)) {
            EXPENSES -> {
                val insertedExpenseId = database.openHelper.writableDatabase.insert(
                    AppDatabase.EXPENSE_TABLE,
                    SQLiteDatabase.CONFLICT_ABORT,
                    values
                )

                database.openHelper.writableDatabase.close()
                return Uri.Builder().authority(AUTHORITY)
                    .path("${EXPENSES_PATH}/${insertedExpenseId}").build()
            }
            else -> throw Exception()
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            EXPENSES -> database.query(
                SupportSQLiteQueryBuilder.builder(AppDatabase.EXPENSE_TABLE).orderBy(sortOrder)
                    .columns(projection).selection(selection, selectionArgs).create()
            )

            else -> throw Exception()
        }


    }

    private companion object {
        const val EXPENSES = 1
        const val EXPENSES_ID = 2
        const val AUTHORITY = "pl.edu.pja.pro1"
        const val EXPENSES_PATH = "expenses"
        const val EXPENSES_ID_PATH = "expenses/#"
        const val MIME_CURSOR_LIST =
            "vnd.android.cursor.item/vnd.${AUTHORITY}.${AppDatabase.EXPENSE_TABLE}"
        const val MIME_CURSOR =
            "vnd.android.cursor.item/vnd.${AUTHORITY}.${AppDatabase.EXPENSE_TABLE}"
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ContentProviderEntryPoint {
    fun provideAppDatabase(): AppDatabase
    fun provideExpenseRepository(): ExpenseRepository
}
