
package edu.cwru.nashua.pathwaysservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the edu.cwru.nashua.pathwaysservice package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SoapPathway_QNAME = new QName("http://nashua.cwru.edu/PathwaysService/", "SoapPathway");
    private final static QName _Boolean_QNAME = new QName("http://nashua.cwru.edu/PathwaysService/", "boolean");
    private final static QName _String_QNAME = new QName("http://nashua.cwru.edu/PathwaysService/", "string");
    private final static QName _ArrayOfSoapPathway_QNAME = new QName("http://nashua.cwru.edu/PathwaysService/", "ArrayOfSoapPathway");
    private final static QName _ArrayOfSoapProcess_QNAME = new QName("http://nashua.cwru.edu/PathwaysService/", "ArrayOfSoapProcess");
    private final static QName _ArrayOfSoapCompartment_QNAME = new QName("http://nashua.cwru.edu/PathwaysService/", "ArrayOfSoapCompartment");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: edu.cwru.nashua.pathwaysservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CurrentUserResponse }
     * 
     */
    public CurrentUserResponse createCurrentUserResponse() {
        return new CurrentUserResponse();
    }

    /**
     * Create an instance of {@link GetGraphData }
     * 
     */
    public GetGraphData createGetGraphData() {
        return new GetGraphData();
    }

    public GetSBModelByID createGetSBModelByID() {
        return new GetSBModelByID();
    }

    /**
     * Create an instance of {@link LoginResponse }
     * 
     */
    public LoginResponse createLoginResponse() {
        return new LoginResponse();
    }

    /**
     * Create an instance of {@link SoapPathway }
     * 
     */
    public SoapPathway createSoapPathway() {
        return new SoapPathway();
    }

    /**
     * Create an instance of {@link GetOrganismHierarchy }
     * 
     */
    public GetOrganismHierarchy createGetOrganismHierarchy() {
        return new GetOrganismHierarchy();
    }

    /**
     * Create an instance of {@link SoapObject }
     * 
     */
    public SoapObject createSoapObject() {
        return new SoapObject();
    }

    /**
     * Create an instance of {@link GetOrganismHierarchyResponse }
     * 
     */
    public GetOrganismHierarchyResponse createGetOrganismHierarchyResponse() {
        return new GetOrganismHierarchyResponse();
    }

    /**
     * Create an instance of {@link ArrayOfSoapPathway }
     * 
     */
    public ArrayOfSoapPathway createArrayOfSoapPathway() {
        return new ArrayOfSoapPathway();
    }

    /**
     * Create an instance of {@link ProcessNeighborhoodResponse }
     * 
     */
    public ProcessNeighborhoodResponse createProcessNeighborhoodResponse() {
        return new ProcessNeighborhoodResponse();
    }

    /**
     * Create an instance of {@link RetrieveLayout }
     * 
     */
    public RetrieveLayout createRetrieveLayout() {
        return new RetrieveLayout();
    }

    /**
     * Create an instance of {@link LogoutResponse }
     * 
     */
    public LogoutResponse createLogoutResponse() {
        return new LogoutResponse();
    }

    /**
     * Create an instance of {@link GetGenomesForPathwayResponse }
     * 
     */
    public GetGenomesForPathwayResponse createGetGenomesForPathwayResponse() {
        return new GetGenomesForPathwayResponse();
    }

    /**
     * Create an instance of {@link Login }
     * 
     */
    public Login createLogin() {
        return new Login();
    }

    /**
     * Create an instance of {@link InsertBug }
     * 
     */
    public InsertBug createInsertBug() {
        return new InsertBug();
    }
    /**
     * Create an instance of {@link InsertBugresponse}
     * 
     */
    public InsertBugResponse createInsertBugResponse() {
        return new InsertBugResponse();
    }    
    
    /**
     * Create an instance of {@link Version }
     * 
     */
    public Version createVersion() {
        return new Version();
    }

    /**
     * Create an instance of {@link ProcessNeighborhood }
     * 
     */
    public ProcessNeighborhood createProcessNeighborhood() {
        return new ProcessNeighborhood();
    }

    /**
     * Create an instance of {@link SoapProcess }
     * 
     */
    public SoapProcess createSoapProcess() {
        return new SoapProcess();
    }

    /**
     * Create an instance of {@link StoreLayoutResponse }
     * 
     */
    public StoreLayoutResponse createStoreLayoutResponse() {
        return new StoreLayoutResponse();
    }

    /**
     * Create an instance of {@link Logout }
     * 
     */
    public Logout createLogout() {
        return new Logout();
    }

    /**
     * Create an instance of {@link GetPathway }
     * 
     */
    public GetPathway createGetPathway() {
        return new GetPathway();
    }

    /**
     * Create an instance of {@link AllProcessesResponse }
     * 
     */
    public AllProcessesResponse createAllProcessesResponse() {
        return new AllProcessesResponse();
    }

    /**
     * Create an instance of {@link GetGeneMappingForPathwayResponse }
     * 
     */
    public GetGeneMappingForPathwayResponse createGetGeneMappingForPathwayResponse() {
        return new GetGeneMappingForPathwayResponse();
    }

    /**
     * Create an instance of {@link AllPathwaysResponse }
     * 
     */
    public AllPathwaysResponse createAllPathwaysResponse() {
        return new AllPathwaysResponse();
    }

    /**
     * Create an instance of {@link SavePathway }
     * 
     */
    public SavePathway createSavePathway() {
        return new SavePathway();
    }

    /**
     * Create an instance of {@link VersionResponse }
     * 
     */
    public VersionResponse createVersionResponse() {
        return new VersionResponse();
    }

    /**
     * Create an instance of {@link GetGeneMappingForPathway }
     * 
     */
    public GetGeneMappingForPathway createGetGeneMappingForPathway() {
        return new GetGeneMappingForPathway();
    }

    /**
     * Create an instance of {@link AllPathways }
     * 
     */
    public AllPathways createAllPathways() {
        return new AllPathways();
    }

    /**
     * Create an instance of {@link GetPathwayResponse }
     * 
     */
    public GetPathwayResponse createGetPathwayResponse() {
        return new GetPathwayResponse();
    }

    /**
     * Create an instance of {@link RetrieveLayoutResponse }
     * 
     */
    public RetrieveLayoutResponse createRetrieveLayoutResponse() {
        return new RetrieveLayoutResponse();
    }

    /**
     * Create an instance of {@link MakeLinkToPage }
     * 
     */
    public MakeLinkToPage createMakeLinkToPage() {
        return new MakeLinkToPage();
    }

    /**
     * Create an instance of {@link CurrentUser }
     * 
     */
    public CurrentUser createCurrentUser() {
        return new CurrentUser();
    }

    /**
     * Create an instance of {@link StoreLayout }
     * 
     */
    public StoreLayout createStoreLayout() {
        return new StoreLayout();
    }

    /**
     * Create an instance of {@link GetGenomesForPathway }
     * 
     */
    public GetGenomesForPathway createGetGenomesForPathway() {
        return new GetGenomesForPathway();
    }

    /**
     * Create an instance of {@link GetGraphDataResponse }
     * 
     */
    public GetGraphDataResponse createGetGraphDataResponse() {
        return new GetGraphDataResponse();
    }

   public GetSBModelByIDResponse createGetSBModelByIDResponse() {
        return new GetSBModelByIDResponse();
    }

    /**
     * Create an instance of {@link GetGeneMappingForOrganismPathwayResponse }
     * 
     */
    public GetGeneMappingForOrganismPathwayResponse createGetGeneMappingForOrganismPathwayResponse() {
        return new GetGeneMappingForOrganismPathwayResponse();
    }

    /**
     * Create an instance of {@link AllProcesses }
     * 
     */
    public AllProcesses createAllProcesses() {
        return new AllProcesses();
    }

    /**
     * Create an instance of {@link SavePathwayResponse }
     * 
     */
    public SavePathwayResponse createSavePathwayResponse() {
        return new SavePathwayResponse();
    }

    /**
     * Create an instance of {@link MakeLinkToPageResponse }
     * 
     */
    public MakeLinkToPageResponse createMakeLinkToPageResponse() {
        return new MakeLinkToPageResponse();
    }

    /**
     * Create an instance of {@link GetGeneMappingForOrganismPathway }
     * 
     */
    public GetGeneMappingForOrganismPathway createGetGeneMappingForOrganismPathway() {
        return new GetGeneMappingForOrganismPathway();
    }

    /**
     * Create an instance of {@link ArrayOfSoapProcess }
     * 
     */
    public ArrayOfSoapProcess createArrayOfSoapProcess() {
        return new ArrayOfSoapProcess();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SoapPathway }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nashua.cwru.edu/PathwaysService/", name = "SoapPathway")
    public JAXBElement<SoapPathway> createSoapPathway(SoapPathway value) {
        return new JAXBElement<SoapPathway>(_SoapPathway_QNAME, SoapPathway.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nashua.cwru.edu/PathwaysService/", name = "boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<Boolean>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nashua.cwru.edu/PathwaysService/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfSoapPathway }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nashua.cwru.edu/PathwaysService/", name = "ArrayOfSoapPathway")
    public JAXBElement<ArrayOfSoapPathway> createArrayOfSoapPathway(ArrayOfSoapPathway value) {
        return new JAXBElement<ArrayOfSoapPathway>(_ArrayOfSoapPathway_QNAME, ArrayOfSoapPathway.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfSoapProcess }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nashua.cwru.edu/PathwaysService/", name = "ArrayOfSoapProcess")
    public JAXBElement<ArrayOfSoapProcess> createArrayOfSoapProcess(ArrayOfSoapProcess value) {
        return new JAXBElement<ArrayOfSoapProcess>(_ArrayOfSoapProcess_QNAME, ArrayOfSoapProcess.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfSoapCompartment }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://nashua.cwru.edu/PathwaysService/", name = "ArrayOfSoapCompartment")
    public JAXBElement<ArrayOfSoapCompartment> createArrayOfSoapCompartment(ArrayOfSoapCompartment value) {
        return new JAXBElement<ArrayOfSoapCompartment>(_ArrayOfSoapCompartment_QNAME, ArrayOfSoapCompartment.class, null, value);
    }
}
