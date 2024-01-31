package com.asktech.admin.constants;

public interface ErrorValues {
	String SQL_ERROR = "SQL Error:";
	String SQL_DUPLICATE_ID = "SQL Exception, Duplicate Order ID";
	String SAVE_SUCCESS = "Saved Success";
	String ALL_FIELDS_MANDATORY = "All Fields Mandatory";
	String FORM_VALIDATION_FAILED = "Form Validation Failed:";
	String SIGNATURE_MISMATCH = "Signature Mismatch";
	String JSON_PARSE_ISSUE_MERCHANT_REQUEST = "Merchant Request is not in Proper Format";
	String INPUT_BLANK_VALUE = "Input should not contain blank value";
	String MERCHANT_INFORMATION_NOT_FOUND = "Merchant with provided information not found !";
	String MERCHANT_EXITS = "Provided Merchant Name already Present in System";
	String MERCHANT_NOT_FOUND = "Merchant not found / Mapped with provided details ";
	String MERCHANT_ID_NOT_FOUND = "MerchantId not Found, Please enter valid MerchantId. ";
	String PG_NOT_ACTIVE_OR_PGSERVICE_NOT_ACTIVE = "Either PG NOT ACTIVE OR PGService NOT ACTIVE";
	String MERCHNT_NOT_EXISTIS = "Provided MerchantId not found! Proceed ahead with correct merchant details.";
	String MERCHANT_PG_SERVICE_NO_MAPPED = "Merchant not mapped with provided PG Service ";
	String MERCHANT_PG_CONFIG_NOT_FOUND = "Merchant mapped with PG Service but not mapped with PG Details like (Appid and Secret Keys).";
	String MERCHANT_PG_APP_ID_NOT_FOUND = "Merchant PG APP id not found in request .";
	String PG_SERVICE_IS_EMPTY = "PG Service is Empty, Please enter PG-Service Name";
	String MERCHANT_PG_SECRET_NOT_FOUND = "Merchant PG Secret id not found in request .";
	String MERCHANT_PG_NAME_NOT_FOUND = "Merchant PG id not found in System .";
	String MERCHANT_KYC_DETAILS_NOT_FOUND = "Merchant Kyc details does not exist! Please fill KYC detail to proceed ahead. ";
	String PG_NOT_PRESENT = "Provided PG name not found in system.";
	String PG_INFORMATION_NOT_FOUND = "PG with provided information not found !";
	String BANK_DETAILS_NOT_FOUND = "Merchnat Bank Details not found in system.";
	String SERVICE_LIMIT_DETAILS_NOT_FOUND = "Service limit Details not found in system.";
	String EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM ="EMAIL ID already exists in system as ADMIN , Admin can't be a Merchant , Please contact Administrator";
	String MERCHANT_EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM ="EMAIL ID already exists in system ";
	String DATE_PARAMETER_IS_MANDATORY = "DATE Parameter (yyyy-mm-dd) is manadatory, Please fill the Start Date and End Date to Proceed ahead !  ";
	String DATE_FORMAT = "Please fill the Correct Date Parameter in (yyyy-mm-dd) Format to Proceed ahead !  ";
	String MERCHANT_PHONE_NUMBER_ALREADY_EXISTS_IN_SYSTEM ="PHONE NUMBER already exists in system ";
	String MERCHANT_KYC_HAS_BEEN_APPROVED = "Merchant Kyc details can not be updated once they have been approved! Contact to Administrator. ";
	
	String MICR_VAIDATION_FILED = "Input MICR format is not valid";
	String IFSC_VAIDATION_FILED = "Input IFSC format is not valid";
	String ACCOUNT_NUMBER_VAIDATION_FILED = "Input ACCOUNT NUMBER format is not valid";
	String GSTID_VALIDATION_FAILED = "Input GST is not proper as per GST Validator. Proceed with proper GST id.";
	String PAN_VALIDATION_FAILED = "Input PAN is not proper as per Pan Validator. Proceed with proper PAN Number.";
	String TAN_VALIDATION_FAILED = "Input TAN is not proper as per Tan Validator. Proceed with proper TAN Number.";
	String MOBILE_VALIDATION_FAILED = "Input Mobile Number is not proper as per Mobile Number Validator. Proceed with proper Mobile Number.";
	String WEBSITE_URL_VALIDATION_FAILED = "Input Web Url is not proper as per Website Validator. Proceed with proper URL.";
	String PIN_VALIDATION_FAILED = "Input PIN is not proper as per PIN Validator. Proceed with proper PIN Number.";
	String EMAIL_VALIDATION_FAILED = "Input EMAIL is not proper as per Email Validator. Proceed with proper Email id.";
	String NAME_VALIDATION_FAILED = "Input Customer Name / Name validation failed .";
	String PHONE_VAIDATION_FAILED = "Input Mobile format is not valid";
	String EMAIL_ID_NOT_FOUND = "Input Email Id not Exists in System , please login with proper EMAIL id / Register as a merchant.";
	String USER_STATUS_BLOCKED = "User Status is Blocked in system , Contact Admistrator.";
	String MERCHANT_STATUS_BLOCKED ="Merchant Status is Blocked in system , Contact Admistrator.";
	String USER_STATUS_REMOVED = "User is Removed from system , Contact Administrator.";
	String PASSWORD_CANT_BE_BLANK = "User Input password can't be NULL";
	String PASSWORD_MISMATCH = "Input password is not correct as per system Information.";
	String SESSION_NOT_FOUND = "User Session not active / found .";
	String SESSION_DEAD = "User is logged out .";
	String USER_NOT_EXISTS = "User does not exist";
	String INITIAL_PASSWORD_CHANGE_REQUEST = "Initial password change required , please check the password . ";
	String PASSWORD_VALIDATION = "Password not validate as per Rules .\n a. start-of-string \n b.a digit must occur at least once \n c.a lower case letter must occur at least once \n d. an upper case letter must occur at least once \n e. a special character must occur at least once \n f. no whitespace allowed in the entire string \n g. anything, at least eight places though \n h. end-of-string";
	String AMOUNT_VALIDATION_ERROR = "Input Amount value is not correct.";
	String SMS_SEND_ERROR = "The SMS process has exception , Please contact administrator.";
	String SMS_OTP_COUNTER_REACHED = "You can try only 3 attempts within 5 minutus Please try after 5 minutes !!";
	String OPT_EXPIRED = "otp is expired please login again !!";
	
	String MERCHANT_BANK_DETAIL_PRESENT = "Merchant Bank details already exists , client can update the bank details from Update option.";
	String MERCHANT_KYC_DETAIL_PRESENT = "Merchant Kyc details already exists !";
	String MERCHANT_SERVICE_TYPE = "Service Type value blank will not be acceptable in system.";
	String MERCHANT_COMMISSION_TYPE = "CommissionType value blank will not be acceptable in system.";
	String MERCHANT_COMM_AMOUNT = "Commission strucure Fixed / Floating amount can't be blank .";
	String MERCHANT_COMMISSION_EXISTS = "Commission structure is avalable for the merchant with service ID and PG Details..";
	
	String ADMIN_USER_EXISTS = "The EMAIL ID alread registered with user ...";
	String SUPER_USER_ROLE = "This user does not have supur user role , only super user can perform the Acivity.";
	
	String PG_ALREADY_CREATED = "PG details already exists as per Input provided , please crosscheck the sensitive information." ;
	String PG_NAME_ALREADY_CREATED ="PG with this name already created in DB";
	String PG_SERVICES = "Input PG services not defined in system , contact Administrator.";
	String PG_SERVICES_ALREADY_DEFINED = "Input PG services already defined in system , contact Administrator.";
	String PG_DEFAULT_SERVICE_SCOPE= "Only one service will be default for all PG associated serviceType";
	String PG_SERVICE_PRESENT = "PG Service Association already found[already exists]";
	
	
	String MERCHANT_PG_ASSOCIATION_EXISTS = "The Association Between Merchant and PG already present.";
	String MERCHANT_PG_ASSOCIATION_NON_EXISTS = "The Association Between Merchant and PG not Found.";
	String MERCHANT_PG_STATUS_BLOCKED = "MERCHANT PG STATUS is BLOCKED";
	String PG_NOT_CREATED = "The Input PG details not found in System.";
	String PG_NOT_ACTIVE = "The Input PG details not ACTIVE in System.";
	String PG_SEVICE_CREATED = "The Mentioned service already associated with PG.";
	String PG_SERVICE_ASSOCIATION_NOT_FOUND = "PG and Provided services association not found.";
	String PG_SERVICE_NOT_FOUND = "No PG service avaiable with input services.";
	String MERCHANT_PG_SERVICE_NOT_ASSOCIATED = "Merchant PG Service Not Found.";
	String MERCHANT_PG_SERVICE_NOT_FOUND = "The service either not active with PG and Merchants / not not associated";
	String MERCHANT_SERVICE_PRESENT_AS_ACTIVE = "Provided PG service details already associated with Merchant in ACTIVE Status!";
	
	String USER_STATUS = "The input user status is not defined in system.";
	String KYC_STATUS = "The input kyc status is not defined in system.";
	String TICKET_STATUS = "The input complaint status is not defined in system.";
	String REFUND_STATUS = "The input refund status is not defined in system.";
	String OTP_MISMATCH = "The provided OTP is not valid for the user.";
	String OTP_EXPIRED = "The otp is expired please login again !!";
	

	String MERCHANT_OTP_EXPIRED = "The otp is expired please send OTP again !!";
	
	String COMPLAINT_TYPE_EXISTS = "The mentioned complaint type already present in system.";
	String COMPLAINT_TYPE_NOT_EXISTS = "The mentioned complaint type not present in system.";
	String COMPLAINT_TYPE_SUBTYPE_EXISTS = "The mentioned complaint type and Subtype already present in system.";
	String COMPLAINT_TYPE_SUBTYPE_NOT_EXISTS = "The mentioned complaint type and Subtype not present in system.";
	String COMPLAINT_ID_BLANK = "Input complaint ID should provided during complaint update operation.";
	String COMPLAINT_NOT_FOUND = "Provided Complaint id not found .";
	String COMPLAINT_ALREADY_CLOSED = "The provided Ticket already in closed state , please reopen the ticket/ create another new Ticket.";
	String COMPLAINT_NOT_CLOSED = "The provided Ticket not in closed status.";
	String COMPLAINT_TYPE_SUB_TYPE_STATUS = "The input Complaint type and Sub Type is not found as Active in system.";
	
	String DUPLICATE_ORDERID = "Order Id Already used";
	String INVALID_COOKIE = "Invalid Cookie Exception";
	String UNKNOWN_OPTION = "Invalid Option";
	
	String DATE_FORMAT_VALIDATION = "Date validaton failed , Date Format should be [DD-MM-YYYY] like [23-09-2021]";

	String DATA_INVALID = "Invalid data in input field";
	String  BANK_ERROR = "BANK Not Found";
	String CREATE_REQUEST_FAILED = "Create request failed";
	
	String DECRYPTION_ERROR = "Decription issue found , please contact administrator.";
	
	String VALIDATION_ERROR_ORDER_AMOUNT = "Input Order Amount can't be negetive or 0";
	String VALIDATION_ERROR_PAYMENT_OPTION = "Input payment Option is not valid.";
	String VALIDATION_ERROR_CARD_NUMBER = "Input card number is not valid.";
	String VALIDATION_ERROR_CARD_HOLDER_NAME = "Input Card Holder name is not valid";
	
	String EXCEPTION_IN_SMS_SENDING = "SMS URL is not working" ;
	
	String MERCHANT_ASSO_WITH_BUSI_ASSO = "Merchant is already associated with one of Business Associate ";
	String BUSINESS_ASSOCIATED_NOT_FOUND = "The Business Associate Information is not found for uuid";
	String COMMISSION_WITH_MERCHANT_ALREADY_PRESENT = "The Commission Structure already present for the Merchant";
	String COMMISSION_UPDATE = "Only Active Commission can be blocked from UI";
	String COMMISSION_NOT_FOUND = "Commission Details not found/ not Active for provided and Commission Id and Business Associate";
	String TRANSACTION_NOT_FOUND = "Transaction details not found / not eligible for Commission update";
	String AMOUNT_NOT_MATCHED_WITH_EDITED_COMM = "Editing commission is not performed due to calculation is not correct , please check the values once.";
	
	String REFUND_INITIATE_FAILED = "Merchant is not eligible to initiate the refund, Please Check Transaction Status for provided detail.";
	String REFUND_UPDATE_FAILED = "Merchant is not eligible to update the refund, Please Check Refund Status for provided details.";
	String REFUND_DETAILS_EXIST = "Refund has already been initiated against this merchant order id , Go to dashboard for more information !";
	String MERCHANT_ORDER_ID_VALIDATION = "Please enter valid merchant order id, For Example : '2345347251167093' ";
	
	String  CHECKSUM_MISMATCH = "CheckSum mismatch happened during selection journey.";

	String MERCHANT_DISBRUSHMENT_ACCOUNT_NOT_FOUND_E0200 = "Merchant Disbrushment account not found , please contact administrator.";
	String FORM_VALIDATION_FAILED_E0201 = "Form Validation Failed:";
	String IFSC_CODE_VALIDATION_FAILED_E0202 = "As per standard , IFSC code validation failed, please contact administrator";
	String BANK_ACCOUNT_VALIDATION_FAILED_E0203 = "Bank Acocunt number digit should ne numeric.";
	String MERCHANT_BANK_BENEFICIARY_EXITS_E0204 = "The defined beneficiary already exits in your account.";
	String BANK_BENEFICIARY_EXITS_With_ANOTHER_MERCHANT_E0205 = "The defined beneficiary already exits with another Merchnat , you can use associate Bank account option.";
	String MERCHANT_BANK_BENEFICIARY_NOT_FOUND_E0206 = "Bank beneficiary details not found as per request data.";
	String MERCHANT_BANK_BENEFICIARY_DELETED_E0207 = "The request beneficisary details already in DELETED state.";
	String DUPLICATE_ORDER_ID_E0208 = "Duplicate Order id from Merchnat.";
	String BENEFICIARY_ALREADY_VERIFIED_E0209 = "The beneficiary account already verified. You can perform/choose re-verify option.";
	String INTERNAL_SERVER_ERROR_E0500 = "Internal Server error contact Administrator.";
	String ORDER_ID_EXITS_WITH_LINK = "One link already created with this order id";
	String ORDER_ID_NOT_FOUND ="Data with entered input not found in the system. Provide correct input to proceed ahead!";
	String REFUND_ID_NOT_FOUND ="Entered Refund Id not found in the system.";
	String EXITS_RESEND_EMAIL_LINK_COUNTER = "You have crossed the limit to resend link.";
	String RESEND_EMAIL_NOT_POSSIBLE= "Resend email link will not possible for this request.";
	String SOURCE_NOT_VALID = "Input source is not valid as per request from Merchant.";
	
	String BANKLIST_EMPTY = "Bank Code should not be emtpy .";
	String WALLETLIST_EMPTY = "Wallet Code should not be emtpy .";
	String MERCHANT_ID_REQUIRED = "Merchant ID is a mandatory parameter.";
	String PG_ID_REQUIRED = "PG ID is required as its a mandatory parameter.";
	String STATUS_NOT_FOUND	="Input status not valid.";
	String BANKLIST_NOT_FOUND = "As per Input BankList not found in System.";

	String TICKET_NOT_FOUND = "Data with entered input not found!";
	String INVALID_FILE_SIZE = "Invalid File Size";
	String INFORMATION_EXISTS = "Provided Merchant information already exists in the system!";
	String MERCHNT_NOT_EXISTIS_FOR_PAYOUT = "Provided merchant details not associated with payout, Please Associate Merchant for Payout Process!";
	String INVALID_IP_ADDRESS = "Invalid input IP address found as per IP Validator! Proceed with proper IP address format.";
	
	String FILE_NOT_FOUND = "Report file not found in Server Directory.";
	String REPORT_NOT_FOUND = "Required report not found in DB.";
	
	String PG_SECRET_KEY_DUPLICATE = "PgSecretKey is already present in DB";
	String PG_SECRET_KEY_NOT_VALID = "PgSecretKey is not valid Alpha-Numeric";
	String PG_SALT_KEY_NOT_VALID = "PgSaltKey is not valid" ; 
	String PG_SALT_KEY_DUPLICATE = "PgSaltKey is already present in DB" ; 
	String PG_APP_ID_DUPLICATE = "PgAppId  is already present in DB";
	String PG_API_EXISTS = "PgApi is already present in DB";
	String PG_SECRET_ID_ALREADY_EXISTS = "PG secret ID already EXISTS in DB";
	String INPUT_EMPTY_NULL = "Input data can not be empty or null";
	String REPORT_NAME_EMPTY_NULL = "ReportName is mandatory";
	String START_DATE_ERROR = "START date is mandatory";
	String END_DATE_ERROR = "END date is mandatory";
	String INVALID_URL = "URL is invalid";
	String USER_NOT_FOUND = "User not found";
	String MERCHNT_NOT_EXIST = "Merchant do not exist in db";
	String MERCHANT_DISTRIBUTOR_ASSOCIATION_ALREADY_EXIST = "Merchant is already associated with this Distributor";
	String DISTRIBUTOR_NOT_EXIST = "Distributor do not exist in db";
	String JSON_PARSE_ISSUE_DISTRIBUTOR_REQUEST = "DISTRIBUTOR Reuest is not in Proper Format";
	String DISTRIBUTOR_PHONE_NUMBER_ALREADY_EXISTS_IN_SYSTEM = "DISTRIBUTOR PHONE NUMBER already exist in db";
	String PGAppId_EMPTY_OR_NULL = "PGAppId  is EMPTY OR NULL not allowed";
	String PGSecretKey_EMPTY_OR_NULL = "PGSecretKey is EMPTY OR NULL not allowed"; 
	String PGSaltKey_EMPTY_OR_NULL = "PGSaltKey is EMPTY OR NULL not allowed";
	String USERNAME_EMPTY = "USERNAME  is EMPTY not allowed";
	String PGNAME_EMPTY_OR_NULL = "PGNAME is EMPTY OR NULL not allowed"; 
	String UPLOAD_FORMATE_ERROR = "You can upload only jpg, jpeg, png, pdf, docx, csv, xlsx";
	String FILE_PARSING_ERROR = "File parsing error";
	String UPDATE_TXN_STATUS_ERROR = "Minimum one details is required..";
	String TXN_STATUS_NOT_MATCH = "Transaction Status is not matched one of the orderId, Status will be SUCCESS/FAILURE/PENDING/REFUND";
	String INTERNAL_ORDER_ID_CAN_NOT_NULL = "Internal order id can not be null/empty";
	String FILE_NAME_NOT_FOUND = "File name not found please enter valid file name..";
	String CSV_FILE_NOT_VALID = "Invalid csv line .. Please check and correct it.";
	String FILE_TYPE_NOT_MATCHED = "File type is not matched, Please enter PAYOUT/PAYIN only.";
	String BANK_LIST_STATUS = "Bank list is only ACTIVE or DEACTIVE.";
	String LIST_DEACTIVE_FIRST = "You need to DEACTIVE all status from list for this merchant.";
	String LIMIT_POLICY_ERROR = "Policy type accept only MERCHANT/PG/SERVICE. or input fields error.";
}
