//复制到相应位置，也可使用eclipse模板
	@Meaning("备用字段1")
	private String standby1;
	@Meaning("备用字段2")
	private String standby2;
	@Meaning("备用字段3")
	private String standby3;
	@Meaning("备用字段4")
	private String standby4;
	@Meaning("备用字段5")
	private String standby5;
	@Meaning("备用字段6")
	private String standby6;
	@Meaning("备用字段7")
	private String standby7;
	@Meaning("备用字段8")
	private Date standby8;
	@Meaning("备用字段9")
	private Date standby9;
	@Meaning("备用字段10")
	private Date standby10;
 //备用字段
     @Column(name="STANDBY1")
	public String getStandby1() {
		return standby1;
	}

	public void setStandby1(String standby1) {
		this.standby1 = standby1;
	}

	@Column(name="STANDBY2")
	public String getStandby2() {
		return standby2;
	}

	public void setStandby2(String standby2) {
		this.standby2 = standby2;
	}
	
	@Column(name="STANDBY3")
	public String getStandby3() {
		return standby3;
	}

	public void setStandby3(String standby3) {
		this.standby3 = standby3;
	}

	@Column(name="STANDBY4")
	public String getStandby4() {
		return standby4;
	}

	public void setStandby4(String standby4) {
		this.standby4 = standby4;
	}

	@Column(name="STANDBY5")
	public String getStandby5() {
		return standby5;
	}

	public void setStandby5(String standby5) {
		this.standby5 = standby5;
	}

	@Column(name="STANDBY6")
	public String getStandby6() {
		return standby6;
	}

	public void setStandby6(String standby6) {
		this.standby6 = standby6;
	}

	@Column(name="STANDBY7")
	public String getStandby7() {
		return standby7;
	}

	public void setStandby7(String standby7) {
		this.standby7 = standby7;
	}

	@Column(name="STANDBY8")
	public Date getStandby8() {
		return standby8;
	}

	public void setStandby8(Date standby8) {
		this.standby8 = standby8;
	}

	@Column(name="STANDBY9")
	public Date getStandby9() {
		return standby9;
	}

	public void setStandby9(Date standby9) {
		this.standby9 = standby9;
	}

	@Column(name="STANDBY10")
	public Date getStandby10() {
		return standby10;
	}

	public void setStandby10(Date standby10) {
		this.standby10 = standby10;
	}
//查找test替换为对应表
ALTER TABLE [dbo].[test] ADD [STANDBY1] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL
GO

ALTER TABLE [dbo].[test] ADD [STANDBY2] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL
GO

ALTER TABLE [dbo].[test] ADD [STANDBY3] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL
GO

ALTER TABLE [dbo].[test] ADD [STANDBY4] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL
GO

ALTER TABLE [dbo].[test] ADD [STANDBY5] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL
GO

ALTER TABLE [dbo].[test] ADD [STANDBY6] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL
GO

ALTER TABLE [dbo].[test] ADD [STANDBY7] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL
GO

ALTER TABLE [dbo].[test] ADD [STANDBY8] datetime  NULL
GO
ALTER TABLE [dbo].[test] ADD [STANDBY9] datetime  NULL
GO
ALTER TABLE [dbo].[test] ADD [STANDBY10] datetime  NULL
GO
