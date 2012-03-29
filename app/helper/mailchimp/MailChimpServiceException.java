package helper.mailchimp;

public class MailChimpServiceException extends RuntimeException {

	private static final long	serialVersionUID	= 1L;
	private final int			code;

	public MailChimpServiceException(final String message, final int code) {
		super((message == null) || (message.length() == 0) ? null : message);
		this.code = code;
	}

	public enum ErrorType {
		Unknown(Integer.MIN_VALUE), ServerError_MethodUnknown(-32601),
		ServerError_InvalidParameters(-32602), Unknown_Exception(-99),
		Zend_Uri_Exception(-92), PDOException(-91), Avesta_Db_Exception(-91),
		XML_RPC2_Exception(-90), XML_RPC2_FaultException(-90),
		Too_Many_Connections(-50), Parse_Exception(0), User_Unknown(100),
		User_Disabled(101), User_DoesNotExist(102), User_NotApproved(103),
		Invalid_ApiKey(104), User_UnderMaintenance(105),
		User_InvalidAction(120), User_MissingEmail(121),
		User_CannotSendCampaign(122), User_MissingModuleOutbox(123),
		User_ModuleAlreadyPurchased(124), User_ModuleNotPurchased(125),
		User_NotEnoughCredit(126), MC_InvalidPayment(127), List_DoesNotExist(
				200), List_InvalidInterestFieldType(210), List_InvalidOption(
				211), List_InvalidUnsubMember(212), List_InvalidBounceMember(
				213), List_AlreadySubscribed(214), List_NotSubscribed(215),
		List_InvalidImport(220), MC_PastedList_Duplicate(221),
		MC_PastedList_InvalidImport(222), Email_AlreadySubscribed(230),
		Email_AlreadyUnsubscribed(231), Email_NotExists(232),
		Email_NotSubscribed(233), List_MergeFieldRequired(250),
		List_CannotRemoveEmailMerge(251), List_Merge_InvalidMergeID(252),
		List_TooManyMergeFields(253), List_InvalidMergeField(254),
		List_InvalidInterestGroup(270), List_TooManyInterestGroups(271),
		Campaign_DoesNotExist(300), Campaign_StatsNotAvailable(301),
		Campaign_InvalidAbsplit(310), Campaign_InvalidContent(311),
		Campaign_InvalidOption(312), Campaign_InvalidStatus(313),
		Campaign_NotSaved(314), Campaign_InvalidSegment(315),
		Invalid_EcommOrder(330), Absplit_UnknownError(350),
		Absplit_UnknownSplitTest(351), Absplit_UnknownTestType(352),
		Absplit_UnknownWaitUnit(353), Absplit_UnknownWinnerType(354),
		Absplit_WinnerNotSelected(355), Invalid_Analytics(500),
		Invalid_DateTime(501), Invalid_Email(502), Invalid_SendType(503),
		Invalid_Template(504), Invalid_TrackingOptions(505), Invalid_Options(
				506), Invalid_Folder(507), Module_Unknown(550),
		MonthlyPlan_Unknown(551), Order_TypeUnknown(552), Invalid_PagingLimit(
				553), Invalid_PagingStart(554);

		private ErrorType(final int code) {
			this.code = code;
		}

		int	code;

	}

	public ErrorType getErrorType() {
		ErrorType result = ErrorType.Unknown;
		final String message = getMessage();
		if (message != null) {
			for (final ErrorType errorType : ErrorType.values()) {
				if (errorType.code == code) {
					result = errorType;
					break;
				}
			}
		}
		return result;
	}
}
