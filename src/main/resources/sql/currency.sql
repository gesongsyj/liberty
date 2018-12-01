#sql("paginate")
	select *
	from currency
	#set(flag=0)
	#if(qo.name)
		#(flag==0?"where":"and") name like concat("%",#para(qo.name),"%")
		#set(flag=1)
	#end
	#if(qo.code)
		#(flag==0?"where":"and") code=#para(qo.code)
		#set(flag=1)
	#end
	#if(qo.followed)
		#(flag==0?"where":"and") followed=1
		#set(flag=1)
	#end
#end

#sql("paginateToBuy")
	select c.*,cs.id as csId,s.`describe` as `describe`,cs.cutLine as cutLine from currency c RIGHT JOIN currency_strategy cs 
	on cs.currencyId=c.id join strategy s on cs.strategyId=s.id
	#set(flag=0)
	#if(qo.name)
		#(flag==0?"where":"and") c.name like concat("%",#para(qo.name),"%")
		#set(flag=1)
	#end
	#if(qo.code)
		#(flag==0?"where":"and") c.code=#para(qo.code)
		#set(flag=1)
	#end
	#if(qo.cutLine)
		#(flag==0?"where":"and") cs.cutLine is not null
		#set(flag=1)
	#end
#end

#sql("findByCode")
	select *
	from currency
	#set(flag=0)
	#if(code)
		#(flag==0?"where":"and") code = #para(code)
		#set(flag=1)
	#end
#end

#sql("listAll")
	select * from currency
#end

#sql("listAllByCutLine")
	select * from currency where cutLine != null
#end