INSERT INTO DEFAULT_TABLE (
    /*IF dto.aaa != null*/AAA,/*END*/
    /*IF dto.bbb != null*/BBB,/*END*/
    VERSIONNO
) VALUES (
	/*IF dto.aaa != null*//*dto.aaa*/null,/*END*/
	/*IF dto.bbb != null*//*dto.bbb*/null,/*END*/
	/*dto.versionNo*/null
)
