package isi.dan.guias.pedidos.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import isi.dan.guias.pedidos.domain.DetallePedido;
import isi.dan.guias.pedidos.domain.Pedido;

@RestController
@RequestMapping("/api/pedido")
public class PedidoRest {

	 private static final List<Pedido> listaPedidos = new ArrayList<Pedido>();
	 private static Integer ID_GEN = 1;
	 
	 @PostMapping
	 public ResponseEntity<Pedido> crear(@RequestBody Pedido pedidoNuevo){
		 pedidoNuevo.setId(ID_GEN++);
		 listaPedidos.add(pedidoNuevo);
		 return ResponseEntity.ok(pedidoNuevo);
	 }
	 
	 @PostMapping(path = "/{id}/detalle")
	 public ResponseEntity<DetallePedido> agregarItem(@PathVariable Integer id, @RequestBody DetallePedido itemNuevo){
		 if(id>=ID_GEN) return ResponseEntity.notFound().build();
		 Pedido pedido = listaPedidos.get(id);
		 if(itemNuevo.getId()==null) itemNuevo.setId(pedido.getDetalle().size());
		 pedido.getDetalle().add(itemNuevo);
		 return ResponseEntity.ok(itemNuevo);
	 }
	 
	 @PutMapping(path= "/{idPedido}")
	 public ResponseEntity<Pedido> actualizar(@PathVariable Integer idPedido, @RequestBody Pedido pedidoNuevo){
		 OptionalInt indexOpt = IntStream.range(0, listaPedidos.size())
				 .filter(index -> listaPedidos.get(index).getId().equals(idPedido))
				 .findFirst();
		 if(indexOpt.isPresent()) {
			 listaPedidos.set(indexOpt.getAsInt(), pedidoNuevo);
			 return ResponseEntity.ok(pedidoNuevo);
		 }
		 else return ResponseEntity.notFound().build();
	 }
	 
	 @DeleteMapping(path= "/{idPedido}")
	 public ResponseEntity<Pedido> borrar(@PathVariable Integer idPedido){
		 OptionalInt indexOpt = IntStream.range(0, listaPedidos.size())
				 .filter(index -> listaPedidos.get(index).getId().equals(idPedido))
				 .findFirst();
		 if(indexOpt.isPresent()) {
			 listaPedidos.remove(indexOpt.getAsInt());
			 return ResponseEntity.ok().build();
		 }
		 else return ResponseEntity.notFound().build();
	 }
	 
	 @DeleteMapping(path= "/{idPedido}/detalle/{id}")
	 public ResponseEntity<Pedido> borrarItem(@PathVariable Integer idPedido, @PathVariable Integer id){
		 OptionalInt indexOpt = IntStream.range(0, listaPedidos.size())
				 .filter(index -> listaPedidos.get(index).getId().equals(idPedido))
				 .findFirst();
		 if(indexOpt.isPresent()) {
			 Pedido pedidoSeleccionado = listaPedidos.get(indexOpt.getAsInt());
			 List<DetallePedido> itemsPedido = pedidoSeleccionado.getDetalle();
			 OptionalInt indexDetalleOpt = IntStream.range(0, pedidoSeleccionado.getDetalle().size())
					 .filter(index -> itemsPedido.get(index).getId().equals(id))
					 .findFirst();
			 if(indexDetalleOpt.isPresent()) {
				 itemsPedido.remove(indexDetalleOpt.getAsInt());
				 return ResponseEntity.ok().build(); 
			 }
			 else return ResponseEntity.notFound().build();			 
		 }
		 else return ResponseEntity.notFound().build();
	 }
	 
	 @GetMapping(path= "/{idPedido}")
	 public ResponseEntity<Pedido> pedidoPorId(@PathVariable Integer idPedido){
		 Optional<Pedido> pedidoBuscado = listaPedidos.stream()
				 .filter(unPedido -> unPedido.getId().equals(idPedido))
				 .findFirst();
		 return ResponseEntity.of(pedidoBuscado);
	 }
	 
	 
	 //La consigna incluia datos de cliente pero no hay cliente en dominio propuesto
	 @GetMapping
	    public ResponseEntity<List<Pedido>> todos(@RequestParam(required = false) Integer idObra){
		 if(idObra==null) return ResponseEntity.ok(listaPedidos);
		 List<Pedido> pedidosBuscados = listaPedidos.stream()
				 .filter(unPedido -> unPedido.getObra().getId().equals(idObra))
				 .collect(Collectors.toList());
		 return (pedidosBuscados.isEmpty()? ResponseEntity.notFound().build() : ResponseEntity.ok(pedidosBuscados));
	 }
	 
	 @GetMapping(path= "/{idPedido}/detalle/{id}")
	 public ResponseEntity<DetallePedido> itemPorId(@PathVariable Integer idPedido, @PathVariable Integer id){
		 Optional<Pedido> pedidoBuscado = listaPedidos.stream()
				 .filter(unPedido -> unPedido.getId().equals(idPedido))
				 .findFirst();
		 if(pedidoBuscado.isPresent()) {
			 Optional<DetallePedido> itemBuscado = pedidoBuscado.get().getDetalle().stream()
					 .filter(unItem -> unItem.getId().equals(id))
					 .findFirst();
			 return ResponseEntity.of(itemBuscado);
		 }
		 return ResponseEntity.notFound().build();
	 }
}
