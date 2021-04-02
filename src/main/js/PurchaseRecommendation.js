import React, { useState } from "react";
import { Button, Modal } from "react-bootstrap";

const PurchaseRecommendation = ({
  purchaseRecommendation,
  showPurchaseButton
}) => {
  const [show, setShow] = useState(false);

  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

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
        {showPurchaseButton && (
          <td>
            <Button variant="primary" onClick={handleShow}>
              +
            </Button>
          </td>
        )}
      </tr>

      <Modal show={show} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>Modal heading</Modal.Title>
        </Modal.Header>
        <Modal.Body>Woohoo, you're reading this text in a modal!</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>
            Close
          </Button>
          <Button variant="primary" onClick={handleClose}>
            Save Changes
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default PurchaseRecommendation;
