package lucenestuff;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


import org.apache.lucene.document.TextField;

import com.google.protobuf.WireFormat.FieldType;

import edu.carleton.comp4601.dao.DocumentCollection;
import edu.carleton.comp4601.resources.MyMongoDB;



public class MyLucene {

	
	private IndexWriter writer;
	private Directory indexDirectory;
	private IndexSearcher searcher;
	
	private DocumentCollection documents;
	private final String INDEX_DIR_PATH = "C:\\Users\\iamro\\Desktop\\L";
	
	public MyLucene(){
		try {
			
			documents = DocumentCollection.getInstance();
			indexBootup(INDEX_DIR_PATH);
			System.out.println("constructor flag");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//-----------------------------------------------------------INDEX CODE
	public void indexBootup(String indexDirectoryPath) throws IOException {
		System.out.println("bootup flag");
	      //Lucene Directory (aka where the Lucene docs will be saved)
		  indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath)); 
		  
		  //This is stuff to set up our indexer
	      Analyzer	analyzer	=	new	StandardAnalyzer();	
	      IndexWriterConfig iwc	=	new	IndexWriterConfig(analyzer);	
	      								iwc.setOpenMode(OpenMode.CREATE);	
	      writer	=	new	IndexWriter(indexDirectory,	iwc);	//indexer
	      writer.deleteAll();
	      
	}
	
	public void addLuceneDoc(edu.carleton.comp4601.dao.Document datadoc) throws IOException{
		/*
		    URL -- the resolved location of the original document.
			DocID -- the id of the document stored within your database.
			Date -- meaning the date and time when the document was crawled.
			Content -- this is the content returned by the content handler used in the Standard Analyzer. For HTML page this should contain information extracted using JSOUP; e.g., content of paragraph, heading and title tags. For images, this may not contain much.
			Metadata fields -- a field should be created for each piece of meta data; e.g., for a file with a MIME type of image/jpeg the field name would be type and the value would be image/jpeg.
		 */
		Document document = new Document();
		System.out.println("lucene add flag 1" + datadoc.getUrl() + " " +  String.valueOf(datadoc.getId()) + datadoc.getText());
		
		//IF STRINGFEILD DOES NOT WORK, TRY ALTERNATIVE AT THIS SITE: https://stackoverflow.com/questions/40699497/what-is-the-difference-between-field-and-stringfield-in-lucene
		 
		document.add(new TextField("url",      datadoc.getUrl(),                  Store.YES)); //WARNING: StringFeild wont have analyzer, so i dont know what that means
		document.add(new TextField("docID",    String.valueOf(datadoc.getId()),   Store.YES));
		document.add(new TextField("content",  datadoc.getText(),                 Store.YES));
		System.out.println("lucene add flag 2");
		//Gotta have that feild for each metadata
		for(int i = 0; i<datadoc.getMetadata().size(); i++){
			System.out.println("lucene add flag 3." + i);
			document.add(new StringField(datadoc.getMetaname().get(i), datadoc.getMetadata().get(i), Store.YES));
		}
		System.out.println("lucene add flag 4");
		//document.add(new StringField("Metadata", (String) mongoDoc.get("meta"), Field.Store.YES));
		
		
		writer.addDocument(document);
		writer.commit();
	    
	}
	
	public void fillUp() throws IOException{
		System.out.println("fillup start flag");
		List<edu.carleton.comp4601.dao.Document> docs = documents.getDocuments();
		System.out.println(docs.toString());
		
		for(int i=0; i<docs.size(); i++){
			System.out.println("loop: " + docs.get(i).getId() + " " + docs.get(i).getUrl());
			addLuceneDoc(docs.get(i));
		}
				
	}
	
	public void endLucene() throws IOException{
		writer.close();
	}
	
	
	
	
	
	
	//------------------------------------------------------------SEARCH CODE
	public void searchBootup() throws IOException{
		 IndexReader reader = DirectoryReader.open(indexDirectory);
		 searcher = new IndexSearcher(reader);
	}
	
	public TopDocs queryByID(String id) throws ParseException, IOException{
		int n = 10;
		
		QueryParser qp = new QueryParser("docID", new StandardAnalyzer());
	    Query idQuery = qp.parse(id);
	    TopDocs hits = searcher.search(idQuery, n); //find top n hits for query
	    return hits;
	}
	
	public TopDocs queryByContent(String content) throws ParseException, IOException{
		int n = 10;
		
		QueryParser qp = new QueryParser("content", new StandardAnalyzer());
	    Query contentQuery = qp.parse(content);
	    TopDocs hits = searcher.search(contentQuery, n); //find top n hits for query
	    return hits;
	}
	
	public void testerSearcher() throws IOException, ParseException{
		 	TopDocs foundDocs2 = queryByContent("apple");
         
	        System.out.println("Total Results :: " + foundDocs2.totalHits);
	         
	        for (ScoreDoc sd : foundDocs2.scoreDocs)
	        {
	            Document d = searcher.doc(sd.doc);
	            System.out.println(String.format(d.get("content")));
	        }
	}
	
	
	
	

}