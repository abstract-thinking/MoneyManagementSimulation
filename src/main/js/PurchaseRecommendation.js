import React, { useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import axios from "axios";

const PurchaseRecommendation = ({
  purchaseRecommendation,
  showPurchaseButton,
  riskManagementId,
  updateView
}) => {
  const [show, setShow] = useState(false);
  const [data, setData] = useState({
    wkn: purchaseRecommendation.wkn,
    name: purchaseRecommendation.name,
    quantity: purchaseRecommendation.quantity,
    purchasePrice: purchaseRecommendation.price,
    notionalSalesPrice: purchaseRecommendation.notionalSalesPrice
  });

  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  const handleSave = () => {
    const targetUrl = `http://localhost:8080/api/risks/${riskManagementId}`;

    axios
      .post(targetUrl, data)
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
      <tr className="purchase-rec-row">
        <td className="text-content">{purchaseRecommendation.wkn}</td>
        <td className="text-content">{purchaseRecommendation.name}</td>
        <td className="number-content">
          {purchaseRecommendation.rsl.toFixed(2)}
        </td>
        <td className="number-content">{purchaseRecommendation.quantity}</td>
        <td className="number-content">
          {purchaseRecommendation.price.toFixed(2)}
        </td>
        <td className="number-content">
          {purchaseRecommendation.notionalSalesPrice.toFixed(2)}
        </td>
        <td>
          <Button variant="primary" onClick={handleShow}>
            +
          </Button>
        </td>
      </tr>

      <Modal show={show} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>Invement hinzufügen</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group controlId="formBasicWkn">
              <Form.Label>WKN</Form.Label>
              <Form.Control
                type="text"
                value={purchaseRecommendation.wkn}
                readOnly
              />
            </Form.Group>

            <Form.Group controlId="formBasicName">
              <Form.Label>Name</Form.Label>
              <Form.Control
                type="text"
                value={purchaseRecommendation.name}
                readOnly
              />
            </Form.Group>

            <Form.Group controlId="formBasicRsl">
              <Form.Label>RSL</Form.Label>
              <Form.Control
                type="text"
                value={purchaseRecommendation.rsl}
                readOnly
              />
            </Form.Group>

            <Form.Group controlId="formBasicQuantity">
              <Form.Label>Anzahl</Form.Label>
              <Form.Control
                type="text"
                value={data.quantity}
                onChange={e => setData({ ...data, quantity: e.target.value })}
              />
            </Form.Group>

            <Form.Group controlId="formBasicPrice">
              <Form.Label>Preis</Form.Label>
              <Form.Control
                type="text"
                value={data.purchasePrice}
                onChange={e => setData({ ...data, purchasePrice: e.target.value })}
              />
            </Form.Group>

            <Form.Group controlId="formBasicNotionalPrice">
              <Form.Label>Fiktiver Verkaufspreis</Form.Label>
              <Form.Control
                type="text"
                value={purchaseRecommendation.notionalSalesPrice.toFixed(2)}
                readOnly
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>
            Schließen
          </Button>
          <Button variant="primary" onClick={handleSave}>
            Hinzufügen
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default PurchaseRecommendation;
