package com.increff.Service;

import com.increff.Dao.OrderItemDao;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.InvoiceData;
import com.increff.Model.InvoiceItemData;
import com.increff.Pojo.OrderItemPojo;
import com.increff.Pojo.OrderPojo;
import com.increff.Pojo.ProductPojo;
import org.apache.fop.apps.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceApi {
    
    private static StringBuilder fileName;
    ProductApi productApi;
    OrderItemDao orderItemDao;
    private InventoryApi inventoryApi;
    
    private UserApi userApi;
    
    @Autowired
    public InvoiceApi(ProductApi productApi, OrderItemDao orderItemDao,
                      InventoryApi inventoryApi, UserApi userApi) {
        this.productApi = productApi;
        this.orderItemDao = orderItemDao;
        this.inventoryApi = inventoryApi;
        this.userApi = userApi;
    }
    
    public void generateInvoice(List<OrderItemPojo> orderItemPojos, OrderPojo orderPojo) throws URISyntaxException {
        File xslFile = new File(Thread.currentThread().getContextClassLoader().getResource("Template.xsl").toURI());
        String xmlInput = getXmlString(orderItemPojos, orderPojo);
        try {
            createInvoicePdf(xmlInput, xslFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiGenericException("Issue while generating XML for orders");
        }
        
    }
    
    private String getXmlString(List<OrderItemPojo> orderItemPojos, OrderPojo orderPojo) {
        String clientName = userApi.findUserById(orderPojo.getClientId()).getName();
        String customerName = userApi.findUserById(orderPojo.getCustomerId()).getName();
        fileName = new StringBuilder("Invoice_").append(clientName).append("_orderId_").append(orderPojo.getOrderId()).append(".pdf");
        InvoiceData invoiceData = InvoiceData.builder().
                invoiceItemData(this.convertOrderItemToInvoiceOrderItem(orderItemPojos)).customerName(customerName)
                .invoiceNumber(orderPojo.getOrderId()).invoiceDate(new Timestamp(System.currentTimeMillis()).toString())
                .channelOrderId(orderPojo.getChannelOrderId()).clientName(clientName).
                invoiceTotal(orderItemPojos.parallelStream().reduce(0.0, (result, orderItem) ->
                        orderItem.getSellingPricePerUnit() * orderItem.getOrderedQuantity(), Double::sum
                )).build();
        
        StringWriter stringWriter = new StringWriter();
        //Convert to XML String
        
        try {
            JAXBContext context = JAXBContext.newInstance(InvoiceData.class);
            Marshaller marshallerObj = context.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshallerObj.marshal(invoiceData, stringWriter);
        } catch (Exception e) {
            throw new ApiGenericException("Issue while generating XML for orders");
        }
        return stringWriter.toString();
    }
    
    private List<InvoiceItemData> convertOrderItemToInvoiceOrderItem(List<OrderItemPojo> orderItemPojos) {
        List<InvoiceItemData> invoiceItemDataList = new ArrayList<InvoiceItemData>();
        for (OrderItemPojo orderItemPojo : orderItemPojos) {
            ProductPojo productPojo = productApi.findProductByGlobalSkuID(orderItemPojo.getGlobalSkuId());
            InvoiceItemData invoiceItemData = InvoiceItemData.builder()
                    .productName(productPojo.getName()).clientSkuid(orderItemPojo.getGlobalSkuId().toString())
                    .sellingPricePerUnit(orderItemPojo.getSellingPricePerUnit())
                    .amount(orderItemPojo.getSellingPricePerUnit() * orderItemPojo.getAllocatedQuantity())
                    .quantity(orderItemPojo.getAllocatedQuantity()).build();
            
            invoiceItemDataList.add(invoiceItemData);
            inventoryApi.updateInventoryAfterFulfillment(orderItemPojo.getGlobalSkuId(), orderItemPojo.getAllocatedQuantity());
            orderItemPojo.setFulfilledQuantity(orderItemPojo.getAllocatedQuantity());
            orderItemPojo.setAllocatedQuantity(0L);
            orderItemDao.updateSingleOrderItem(orderItemPojo);
        }
        
        return invoiceItemDataList;
    }
    
    private void createInvoicePdf(String xml, File xslt) throws IOException, FOPException, TransformerException {
        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        
        OutputStream outputStream = Files.newOutputStream(Paths.get(fileName.toString()));
        Fop fop = null;
        fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outputStream);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(xslt));
        Source src = new StreamSource(new StringReader(xml));
        Result res = new SAXResult(fop.getDefaultHandler());
        transformer.transform(src, res);
    }
}
