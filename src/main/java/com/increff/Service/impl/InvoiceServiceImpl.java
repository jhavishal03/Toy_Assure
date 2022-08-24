package com.increff.Service.impl;

import com.increff.Dao.OrderItemDao;
import com.increff.Dao.ProductDao;
import com.increff.Dao.UserDao;
import com.increff.Dto.InvoiceData;
import com.increff.Dto.InvoiceItemData;
import com.increff.Exception.ApiGenericException;
import com.increff.Model.Inventory;
import com.increff.Model.Order;
import com.increff.Model.OrderItem;
import com.increff.Model.Product;
import com.increff.Service.InvoiceService;
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
public class InvoiceServiceImpl implements InvoiceService {
    
    private static StringBuilder fileName;
    @Autowired
    ProductDao productDao;
    @Autowired
    OrderItemDao orderItemDao;
    @Autowired
    private InventoryServiceImpl inventoryService;
    @Autowired
    private UserDao userDao;
    
    public void generateInvoice(List<OrderItem> orderItems, Order order) throws URISyntaxException {
        File xslFile = new File(Thread.currentThread().getContextClassLoader().getResource("Template.xsl").toURI());
        fileName = new StringBuilder("Invoice_For_");
        String xmlInput = getXmlString(orderItems, order);
        try {
            createInvoicePdf(xmlInput, xslFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiGenericException("Issue while generating XML for orders");
        }
        
    }
    
    private String getXmlString(List<OrderItem> orderItems, Order order) {
        String clientName = userDao.findUserById(order.getClientId()).getName();
        fileName.append(clientName);
        fileName.append("_orderId_" + order.getOrderId());
        InvoiceData invoiceData = InvoiceData.builder().
                invoiceItemData(this.convertOrderitemToInvoiceOrderItem(orderItems))
                .invoiceNumber(order.getOrderId()).invoiceDate(new Timestamp(System.currentTimeMillis()).toString())
                .clientName(clientName).invoiceTotal(orderItems.stream()
                        .mapToDouble(orderItem ->
                                orderItem.getAllocatedQuantity() * orderItem.getSellingPricePerUnit()).sum()).build();
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
    
    private List<InvoiceItemData> convertOrderitemToInvoiceOrderItem(List<OrderItem> orderItems) {
        List<InvoiceItemData> invoiceItemDataList = new ArrayList<InvoiceItemData>();
        for (OrderItem orderItem : orderItems) {
            Product product = productDao.findProductByGlobalSkuId(orderItem.getGlobalSkuId());
            InvoiceItemData invoiceItemData = InvoiceItemData.builder()
                    .productName(product.getName()).clientSkuid(orderItem.getGlobalSkuId().toString())
                    .sellingPricePerUnit(orderItem.getSellingPricePerUnit())
                    .amount(orderItem.getSellingPricePerUnit() * orderItem.getAllocatedQuantity())
                    .quantity(orderItem.getAllocatedQuantity()).build();
            
            invoiceItemDataList.add(invoiceItemData);
            Inventory inventory = inventoryService.updateInventoryAfterFulfillment(orderItem.getGlobalSkuId(), orderItem.getAllocatedQuantity());
            orderItem.setFulfilledQuantity(orderItem.getAllocatedQuantity());
            orderItem.setAllocatedQuantity(0L);
            orderItemDao.addSingleOrderItem(orderItem);
        }
        
        return invoiceItemDataList;
    }
    
    private void createInvoicePdf(String xml, File xslt) throws IOException, FOPException, TransformerException {
        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        fileName.append(".pdf");
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
