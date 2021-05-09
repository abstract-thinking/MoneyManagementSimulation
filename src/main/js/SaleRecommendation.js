import React, { useState } from "react";
import { Button, Modal } from "react-bootstrap";
import axios from "axios";

const SaleRecommendation = ({
  saleRecommendation,
  riskManagementId,
  exchange,
  updateView
}) => {
  const [show, setShow] = useState(false);

  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  const handleDelete = () => {
    const targetUrl = `http://localhost:8080/api/riskManagements/${riskManagementId}/investments/${saleRecommendation.id}`;

    axios
      .delete(targetUrl)
      .then(response => {
        console.log(response);
        updateView();
      })
      .catch(error => {
        console.log("Error: ", error);
      });

    handleClose();
  };

  return (
    <>
      <tr key={saleRecommendation.company}>
        <td className="text-content">{saleRecommendation.wkn}</td>
        <td className="text-content">{saleRecommendation.name}</td>
        <td className="number-content">{saleRecommendation.rsl}</td>
        <td className="number-content">{saleRecommendation.price}</td>
        <td className="number-content">
          {saleRecommendation.notionalSalesPrice}
        </td>
        <td>
          {saleRecommendation.shouldSellByStopPrice && (
            <div>
              {saleRecommendation.name} wöchentlicher Schlusskurs{" "}
              {saleRecommendation.price} liegt unter dem kalkulierten
              Verkaufspreis {saleRecommendation.notionalSalesPrice}.
            </div>
          )}
          {saleRecommendation.shouldSellByRslComparison && (
            <div>
              {saleRecommendation.name} RSL {saleRecommendation.rsl} liegt unter
              dem {exchange.name} RSL {exchange.rsl}.
            </div>
          )}
        </td>
        <td>
          <Button variant="primary" onClick={handleShow}>
            -
          </Button>
        </td>
      </tr>
      <Modal show={show} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>Eintrag löschen</Modal.Title>
        </Modal.Header>
        <Modal.Body>Soll {saleRecommendation.name} gelöscht werden?</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>
            Schließen
          </Button>
          <Button variant="primary" onClick={handleDelete}>
            Löschen
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default SaleRecommendation;
