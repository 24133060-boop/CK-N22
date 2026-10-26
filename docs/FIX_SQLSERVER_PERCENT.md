Fix SQL Server keyword PERCENT: ShopPromotion.percent maps to quoted column using Hibernate @Column(name="`percent`"). No database change. Clean and restart STS, test /manager/catalog.
