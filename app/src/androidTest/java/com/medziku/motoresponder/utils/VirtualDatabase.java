
class VirtualDatabase extends SQLiteOpenHelper {
public VirtualDatabase(String[] columnNames, String[][] values){
   this.columnNames=columnNames;
   this.values=values;
}


    public void onCreate(SQLiteDatabase db) {
         String creationSQL = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT" + ")";
                
                String creationSQL = "CREATE TABLE MOCKED (";
                for (String columnName:this.columnNames){
                   creationSQL+="columnName+",";
                }
                creationSQL+=")";
    db.execSQL(creationSQL);
    
    this.insertData();
    }
    
    private void insertData(){
           SQLiteDatabase db = this.getWritableDatabase();
           
           for (String[] row : this.values){
           ContentValues values=new ContentValues();
              for (int i = 0; i< this.columnNames.length;i++){
                  values.put(this.columnNames[i], row[i]);
              }
              db.insert("MOCKED", null, values);
           }
           db.close();
           }
    
    public Cursor query(){
   return  db.query("MOCKED", new String[] { KEY_ID,
                KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
    }
}
