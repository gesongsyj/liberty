#sql("getByCurrency")
	select *
	from strategy s join currency_strategy cs on s.id=cs.strategyId
	#set(flag=0)
	#if(currencyId)
		#(flag==0?"where":"and") cs.currencyId=#para(currencyId)
		#set(flag=1)
	#end
#end

#sql("getAll")
	select * from strategy
#end