document.addEventListener("DOMContentLoaded", ()=>{
	flyttAlleBrikker();
});

function flyttAlleBrikker(){
	const brikker=document.querySelectorAll('.spillerBrikke');
	
	brikker.forEach(brikke=>{
		
		const startPosisjon = parseInt(brikke.getAttribute('data-start-posisjon'))||1;
		const sluttPosisjon = parseInt(brikke.getAttribute('data-posisjon'))||1;
		
		brikke.style.transistion='none';
		
		const startKoordinater= beregnKoordinater(startPosisjon);
		
		brikke.style.left=startKoordinater.left +'%';
		brikke.style.bottom = startKoordinater.bottom + '%';
		
		setTimeout(()=>{
			brikke.style.transistion='all 0.6 cubic-bezier(0.25, 0.8, 0.25, 1)';
			
			const sluttKoordinater=beregnKoordinater(sluttPosisjon);
			brikke.style.left = sluttKoordinater.left + '%';
			brikke.style.bottom = sluttKoordinater.bottom + '%';
		}, 50);
		
	});
}
	
function beregnKoordinater(posisjon){
	const index = posisjon -1;
	const rad= Math.floor(index/10);
	let kolonne = index%10;
		
	if((rad%2)==1){
			kolonne=9-kolonne;
	}
	return {
		left:kolonne*10,
		bottom: rad*10
	};                           
}