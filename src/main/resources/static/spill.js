document.addEventListener("DOMContentLoaded", ()=>{
	flyttAlleBrikker();
});

function flyttAlleBrikker(){
	document.querySelectorAll('.spillerBrikke');
	
	brikker.forEach(brikke=>{
		let posisjon = parseInt(brikke.getAttributeData('data-posisjon'));
		
		if(!posisjon || posisjon<1) {
			posisjon=1;
		}
		if(posisjon>100){
			posisjon=100;
		}
		
		const kooridinater= beregnKoordinater(posisjon);
		
		brikke.style.left=koordinater.left +'%';
		brikke.style.bottom = koordinater + '%';
	})
	
	function beregnKoordinater(posisjon){
		const index = posisjon -1;
		const rad= Math.floor(index/10);
		let kolonne = index%10;
		
		if((rad%2)==1){
			kolonne=rad-9;
		}
		
		return {
			left:kolonne*10,
			bottom: rad*10
		};
		
		
	}
}