package eBay;

import java.io.FileWriter;
import java.util.List;

import org.json.JSONWriter;

import com.ebay.services.client.ClientConfig;
import com.ebay.services.client.FindingServiceClientFactory;
import com.ebay.services.finding.FindItemsByKeywordsRequest;
import com.ebay.services.finding.FindItemsByKeywordsResponse;
import com.ebay.services.finding.FindingServicePortType;
import com.ebay.services.finding.PaginationInput;
import com.ebay.services.finding.SearchItem;

public class Crawl {

	public void crawl() {
		try {
            // initialize service end-point configuration
			ClientConfig config = new ClientConfig(); config.setApplicationId("Personal-0cc9-4dc8-a74a-74b1986613a9");
			//create a service client
			FindingServicePortType serviceClient = FindingServiceClientFactory.getServiceClient(config);
			//create request object
			FindItemsByKeywordsRequest request = new FindItemsByKeywordsRequest(); //set request parameters
			request.setKeywords("t shirt");
			PaginationInput pi = new PaginationInput();
			pi.setEntriesPerPage(100);
			FileWriter file = new FileWriter("BasicInfo_100.json", true); 
			List<SearchItem> items;
			for(int i = 1; i <= 5000; i++) {
				pi.setPageNumber(i);
				request.setPaginationInput(pi);
				//call service
				FindItemsByKeywordsResponse result = serviceClient.findItemsByKeywords(request);
				//output result
				System.out.println("Ack = "+result.getAck()); 
				System.out.println("Find " + result.getSearchResult().getCount() + " items." );
				System.out.println("Page = "+result.getPaginationOutput().getPageNumber() + "/" + result.getPaginationOutput().getTotalPages()); 
				items = result.getSearchResult().getItem();
				for(SearchItem item : items) {
					new JSONWriter(file).object().key(item.getItemId()).value(item.getTitle()).endObject();
					new JSONWriter(file).object().key(item.getItemId()).value(item.getGalleryURL()).endObject();
					//new JSONWriter(file).object().key("Title").value(item.getTitle()).endObject();
					//new JSONWriter(file).object().key("ImageURL").value(item.getGalleryURL()).endObject();
					//new JSONWriter(file).object().key("ViewURL").value(item.getViewItemURL()).endObject();
				}
			}
			file.flush();
		} catch (Exception ex) {
			// handle exception if any
			ex.printStackTrace();
		}
	}

}
