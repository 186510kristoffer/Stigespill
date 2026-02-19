document.addEventListener("DOMContentLoaded", ()=>{
	flyttAlleBrikker();
});

function flyttAlleBrikker(){
	const brikker=document.querySelectorAll('.spillerBrikke');
	
	brikker.forEach(brikke=>{
		let posisjon = parseInt(brikke.getAttribute('data-posisjon'));
		
		if(!posisjon || posisjon<1) {
			posisjon=1;
		}
		if(posisjon>100){
			posisjon=100;
		}
		
		const koordinater= beregnKoordinater(posisjon);
		
		brikke.style.left=koordinater.left +'%';
		brikke.style.bottom = koordinater.bottom + '%';
	})
	
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
}